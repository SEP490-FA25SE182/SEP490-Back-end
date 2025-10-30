package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.AIGenerationResponse;
import com.sep.aiservice.dto.AIGenerationTargetResponse;
import com.sep.aiservice.dto.GenerateIllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import com.sep.aiservice.entity.AIGeneration;
import com.sep.aiservice.entity.AIGenerationTarget;
import com.sep.aiservice.entity.Illustration;
import com.sep.aiservice.enums.AIGenerationEnum;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.mapper.AIGenerationMapper;
import com.sep.aiservice.mapper.AIGenerationTargetMapper;
import com.sep.aiservice.mapper.IllustrationMapper;
import com.sep.aiservice.repository.AIGenerationRepository;
import com.sep.aiservice.repository.AIGenerationTargetRepository;
import com.sep.aiservice.repository.IllustrationRepository;
import com.sep.aiservice.service.AiGenerationLogService;
import com.sep.aiservice.service.IllustrationGenerationService;
import com.sep.aiservice.service.StorageService;
import jakarta.annotation.Nullable;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.firstNonBlank;

@RequiredArgsConstructor
@Transactional
@Service
public class IllustrationGenerationServiceImpl implements IllustrationGenerationService {

    @Qualifier("stabilityWebClient")
    private final WebClient stabilityWebClient;

    @Value("${stability.api-key}")
    private String stabilityApiKey;

    private final IllustrationRepository illustrationRepo;
    private final StorageService storage;
    private final AiGenerationLogService genLog;
    private final IllustrationMapper illustrationMapper;

    @Value("${stability.default-model:sd3.5-flash}")
    private String defaultModel;
    @Value("${stability.client.id}")
    private String appClientId;
    @Value("${stability.client.version}")
    private String appClientVersion;
    @Value("${stability.default-accept:image/*}")
    private String defaultAccept;

    private static final String CORE_URL = "https://api.stability.ai/v2beta/stable-image/generate/core";


    @Override
    public IllustrationResponse generateSync(GenerateIllustrationRequest req,
                                             @Nullable MultipartFile controlImage,
                                             @Nullable String userId) {
        long started = System.nanoTime();
        AIGeneration gen = genLog.begin(firstNonBlank(req.getModelName(), defaultModel),
                req.getPrompt(),
                req.getMode()==null?GenerationMode.TEXT_TO_IMAGE:req.getMode(),
                StringUtils.hasText(req.getAccept())?req.getAccept():defaultAccept,
                userId);

        try {
            byte[] imageBytes = callStability(req, controlImage, userId);
            if (imageBytes == null || imageBytes.length == 0) {
                throw new IllegalStateException("No image bytes returned from Stability. " +
                        "Make sure 'accept' is 'image/*' and request parameters are valid.");
            }

            String ext = Optional.ofNullable(req.getFormat()).orElse("png").toLowerCase();
            String fileName = "illustrations/illu-" + gen.getAiGenerationId() + "-" + System.currentTimeMillis() + "." + ext;
            String url = storage.save(fileName, imageBytes, "image/" + ext);

            Illustration illu = new Illustration();
            illu.setImageUrl(url);
            illu.setStyle(req.getStyle());
            illu.setFormat(ext);
            illu.setWidth(req.getWidth());
            illu.setHeight(req.getHeight());
            illu.setTitle(req.getTitle());
            illu.setIsActived(IsActived.ACTIVE);
            illu = illustrationRepo.save(illu);

            genLog.linkTarget(gen, "ILLUSTRATION", illu.getIllustrationId());
            genLog.success(gen, (System.nanoTime() - started)/1_000_000.0);

            return illustrationMapper.toResponse(illu);

        } catch (Exception ex) {
            genLog.fail(gen, (System.nanoTime() - started)/1_000_000.0, ex);
            throw ex;
        }
    }

    private byte[] callStability(GenerateIllustrationRequest req,
                                 @Nullable MultipartFile controlImage,
                                 @Nullable String userId) {
        boolean useControl = controlImage != null && !controlImage.isEmpty()
                && StringUtils.hasText(req.getControlnetType());
        return useControl
                ? callControlNet(req, controlImage, userId)
                : callTextToImage(req, userId);
    }


    private byte[] callTextToImage(GenerateIllustrationRequest req, @Nullable String userIdForHeader) {
        MultipartBodyBuilder mb = new MultipartBodyBuilder();

        mb.part("prompt", req.getPrompt());
        if (StringUtils.hasText(req.getNegativePrompt())) mb.part("negative_prompt", req.getNegativePrompt());
        if (req.getSeed() != null)        mb.part("seed", String.valueOf(req.getSeed()));
        if (req.getCfgScale() != null)    mb.part("cfg_scale", String.valueOf(req.getCfgScale()));
        if (req.getStylePreset() != null) mb.part("style_preset", req.getStylePreset().name().toLowerCase().replace('_','-'));
        if (req.getWidth() != null && req.getHeight() != null) {
            mb.part("width",  String.valueOf(req.getWidth()));
            mb.part("height", String.valueOf(req.getHeight()));
        } else if (StringUtils.hasText(req.getAspectRatio())) {
            mb.part("aspect_ratio", req.getAspectRatio());
        }
        mb.part("output_format", Optional.ofNullable(req.getFormat()).orElse("png"));

        final String accept = StringUtils.hasText(req.getAccept()) ? req.getAccept() : defaultAccept; // khuyên dùng "image/png"
        final String clientUserId = StringUtils.hasText(userIdForHeader) ? userIdForHeader : "anonymous";

        return withLog(stabilityWebClient).post()
                .uri(CORE_URL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> {
                    h.setBearerAuth(Optional.ofNullable(stabilityApiKey).orElse("").trim()); // <-- THÊM DÒNG NÀY
                    h.set(HttpHeaders.ACCEPT, accept);
                    h.set("stability-client-id",      appClientId);
                    h.set("stability-client-user-id", clientUserId);
                    h.set("stability-client-version", appClientVersion);
                })
                .body(BodyInserters.fromMultipartData(mb.build()))
                .exchangeToMono(resp -> {
                    if (!resp.statusCode().is2xxSuccessful()) {
                        return resp.bodyToMono(String.class).defaultIfEmpty("<empty>")
                                .flatMap(b -> Mono.error(new IllegalStateException(
                                        "Stability error " + resp.statusCode() + " at " + CORE_URL + ": " + b)));
                    }
                    return resp.bodyToFlux(DataBuffer.class)
                            .reduce(new java.io.ByteArrayOutputStream(), (baos, db) -> {
                                try {
                                    var nio = db.asByteBuffer().asReadOnlyBuffer();
                                    byte[] chunk = new byte[nio.remaining()];
                                    nio.get(chunk);
                                    baos.write(chunk);
                                } catch (java.io.IOException e) {
                                    throw new RuntimeException("Stream read failed", e);
                                } finally {
                                    DataBufferUtils.release(db);
                                }
                                return baos;
                            })
                            .map(java.io.ByteArrayOutputStream::toByteArray);
                })
                .block();
    }


    private byte[] callControlNet(GenerateIllustrationRequest req,
                                  MultipartFile controlImage,
                                  @Nullable String userIdForHeader) {
        MultipartBodyBuilder mb = new MultipartBodyBuilder();

        mb.part("prompt", req.getPrompt());
        mb.part("controlnet_type", req.getControlnetType());
        mb.part("control_image", toResource(controlImage))
                .filename(Optional.ofNullable(controlImage.getOriginalFilename()).orElse("control.png"));

        if (StringUtils.hasText(req.getNegativePrompt())) mb.part("negative_prompt", req.getNegativePrompt());
        if (req.getSeed() != null)        mb.part("seed", String.valueOf(req.getSeed()));
        if (req.getCfgScale() != null)    mb.part("cfg_scale", String.valueOf(req.getCfgScale()));
        if (req.getStylePreset() != null) mb.part("style_preset", req.getStylePreset().name().toLowerCase().replace('_','-'));
        if (req.getWidth() != null && req.getHeight() != null) {
            mb.part("width",  String.valueOf(req.getWidth()));
            mb.part("height", String.valueOf(req.getHeight()));
        } else if (StringUtils.hasText(req.getAspectRatio())) {
            mb.part("aspect_ratio", req.getAspectRatio());
        }
        mb.part("output_format", Optional.ofNullable(req.getFormat()).orElse("png"));

        final String accept = StringUtils.hasText(req.getAccept()) ? req.getAccept() : defaultAccept;
        final String clientUserId = StringUtils.hasText(userIdForHeader) ? userIdForHeader : "anonymous";

        return withLog(stabilityWebClient).post()
                .uri("/v2beta/stable-image/controlnet")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> {
                    h.setBearerAuth(Optional.ofNullable(stabilityApiKey).orElse("").trim()); // <-- THÊM DÒNG NÀY
                    h.set(HttpHeaders.ACCEPT, accept);
                    h.set("stability-client-id",      appClientId);
                    h.set("stability-client-user-id", clientUserId);
                    h.set("stability-client-version", appClientVersion);
                })
                .body(BodyInserters.fromMultipartData(mb.build()))
                .exchangeToMono(resp -> {
                    if (!resp.statusCode().is2xxSuccessful()) {
                        return resp.bodyToMono(String.class).defaultIfEmpty("<empty>")
                                .flatMap(b -> Mono.error(new IllegalStateException(
                                        "Stability error " + resp.statusCode() + " at /v2beta/stable-image/controlnet: " + b)));
                    }
                    return resp.bodyToFlux(DataBuffer.class)
                            .reduce(new java.io.ByteArrayOutputStream(), (baos, db) -> {
                                try {
                                    var nio = db.asByteBuffer().asReadOnlyBuffer();
                                    byte[] chunk = new byte[nio.remaining()];
                                    nio.get(chunk);
                                    baos.write(chunk);
                                } catch (java.io.IOException e) {
                                    throw new RuntimeException("Stream read failed", e);
                                } finally {
                                    DataBufferUtils.release(db);
                                }
                                return baos;
                            })
                            .map(java.io.ByteArrayOutputStream::toByteArray);
                })
                .block();
    }


    private HttpEntity<Resource> toResource(MultipartFile f) {
        try {
            ByteArrayResource res = new ByteArrayResource(f.getBytes()){
                @Override public String getFilename(){ return f.getOriginalFilename(); }
            };
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.parseMediaType(
                    Optional.ofNullable(f.getContentType()).orElse("image/png")));
            return new HttpEntity<>(res, h);
        } catch (IOException e) {
            throw new RuntimeException("Read control image failed", e);
        }
    }

    private String firstNonBlank(String a, String b) {
        return StringUtils.hasText(a) ? a : b;
    }

    private WebClient withLog(WebClient wc) {
        return wc.mutate().filter((req, next) -> {
            String auth = req.headers().getFirst(HttpHeaders.AUTHORIZATION);
            String masked = (auth == null) ? "<null>" :
                    (auth.length() <= 14 ? auth : auth.substring(0, 14) + "****");
            System.out.println(">>> CALLING: " + req.method() + " " + req.url());
            System.out.println(">>> Authorization: " + masked);
            System.out.println(">>> Accept: " + req.headers().getFirst(HttpHeaders.ACCEPT));
            return next.exchange(req);
        }).build();
    }


}
