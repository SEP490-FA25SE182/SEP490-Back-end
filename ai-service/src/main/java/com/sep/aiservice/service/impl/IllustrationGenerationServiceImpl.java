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
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.firstNonBlank;

@Service
@RequiredArgsConstructor
@Transactional
public class IllustrationGenerationServiceImpl implements IllustrationGenerationService {

    private final WebClient stabilityWebClient;
    private final IllustrationRepository illustrationRepo;
    private final AIGenerationRepository aiGenRepo;
    private final AIGenerationTargetRepository aiTargetRepo;
    private final StorageService storage;

    //inject các mapper
    @Qualifier("illustrationMapper")
    private final IllustrationMapper illustrationMapper;
    @Qualifier("AIGenerationMapper")
    private final AIGenerationMapper aiGenerationMapper;
    @Qualifier("AIGenerationTargetMapper")
    private final AIGenerationTargetMapper aiGenerationTargetMapper;

    @Value("${stability.default-model:sd3.5-flash}")
    private String defaultModel;
    @Value("${stability.client.id}")
    private String appClientId;
    @Value("${stability.client.version}")
    private String appClientVersion;
    @Value("${stability.default-accept:image/*}")
    private String defaultAccept;

    @Override
    public IllustrationResponse generateSync(GenerateIllustrationRequest req,
                                             @Nullable MultipartFile controlImage,
                                             @Nullable String userId) {
        long started = System.currentTimeMillis();

        // 1) Log AIGeneration
        AIGeneration gen = new AIGeneration();
        gen.setModelName(firstNonBlank(req.getModelName(), defaultModel));
        gen.setPrompt(req.getPrompt());
        gen.setNegativePrompt(req.getNegativePrompt());
        gen.setStatus(AIGenerationEnum.PENDING.getStatus());
        gen.setUserId(userId);
        gen.setMode(req.getMode() == null ? GenerationMode.TEXT_TO_IMAGE : req.getMode());
        gen.setAspectRatio(req.getAspectRatio());
        gen.setStrength(req.getStrength());
        gen.setSeed(req.getSeed());
        gen.setCfgScale(req.getCfgScale());
        gen.setStylePreset(req.getStylePreset());
        gen.setAcceptHeader(req.getAccept());

        gen = aiGenRepo.save(gen);

        try {
            // 2) Gọi Stability
            byte[] imageBytes = callStability(req, controlImage, userId);

            // 3) Lưu file -> URL
            String ext = Optional.ofNullable(req.getFormat()).orElse("png").toLowerCase();
            String fileName = "illu-" + gen.getAiGenerationId() + "-" + System.currentTimeMillis() + "." + ext;
            String url = storage.save(fileName, imageBytes, "image/" + ext);

            // 4) Ghi Illustration
            Illustration illu = new Illustration();
            illu.setImageUrl(url);
            illu.setStyle(req.getStyle());
            illu.setFormat(ext);
            illu.setWidth(req.getWidth());
            illu.setHeight(req.getHeight());
            illu.setTitle(req.getTitle());
            illu.setIsActived(IsActived.ACTIVE);
            illu.setUpdatedAt(Instant.now());
            illu = illustrationRepo.save(illu);

            // 5) Target
            AIGenerationTarget tgt = new AIGenerationTarget();
            tgt.setAiGenerationId(gen.getAiGenerationId());
            tgt.setTargetType("ILLUSTRATION");
            tgt.setTargetRefId(illu.getIllustrationId());
            tgt.setUpdatedAt(Instant.now());
            tgt = aiTargetRepo.save(tgt);

            // 6) Cập nhật gen
            gen.setDurationMs(System.currentTimeMillis() - started);
            gen.setStatus(AIGenerationEnum.SUCCESS.getStatus());
            gen = aiGenRepo.save(gen);

            AIGenerationResponse genResp = aiGenerationMapper.toResponse(gen);
            AIGenerationTargetResponse tgtResp = aiGenerationTargetMapper.toResponse(tgt);
            return illustrationMapper.toResponse(illu);

        } catch (Exception ex) {
            gen.setDurationMs(System.currentTimeMillis() - started);
            gen.setStatus(AIGenerationEnum.FAILED.getStatus());
            aiGenRepo.save(gen);
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
        if (req.getSeed()!=null)                         mb.part("seed", req.getSeed());
        if (req.getWidth()!=null)                        mb.part("width", req.getWidth().toString());
        if (req.getHeight()!=null)                       mb.part("height", req.getHeight().toString());
        if (req.getCfgScale()!=null)                     mb.part("cfg_scale", req.getCfgScale().toString());
        if (req.getStylePreset()!=null)                  mb.part("style_preset", req.getStylePreset().name().toLowerCase().replace('_','-'));
        if (StringUtils.hasText(req.getAspectRatio()))   mb.part("aspect_ratio", req.getAspectRatio());
        mb.part("output_format", Optional.ofNullable(req.getFormat()).orElse("png"));
        mb.part("model", firstNonBlank(req.getModelName(), defaultModel));

        // Headers:
        String accept = StringUtils.hasText(req.getAccept()) ? req.getAccept() : defaultAccept;
        String clientUserId = StringUtils.hasText(userIdForHeader) ? userIdForHeader : "anonymous";

        return stabilityWebClient.post()
                .uri("/v2beta/stable-image/generate")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> {
                    h.set(HttpHeaders.ACCEPT, accept);
                    h.set("stability-client-id", appClientId);
                    h.set("stability-client-user-id", clientUserId);
                    h.set("stability-client-version", appClientVersion);
                })
                .body(BodyInserters.fromMultipartData(mb.build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(b -> Mono.error(new IllegalStateException("Stability error: " + b))))
                .bodyToMono(byte[].class)
                .block();
    }

    private byte[] callControlNet(GenerateIllustrationRequest req,
                                  MultipartFile controlImage,
                                  @Nullable String userIdForHeader) {
        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        mb.part("prompt", req.getPrompt());
        if (StringUtils.hasText(req.getNegativePrompt()))
            mb.part("negative_prompt", req.getNegativePrompt());
        mb.part("controlnet_type", req.getControlnetType());
        mb.part("output_format", Optional.ofNullable(req.getFormat()).orElse("png"));
        if (req.getSeed()!=null)          mb.part("seed", req.getSeed().toString());
        if (req.getWidth()!=null)         mb.part("width", req.getWidth().toString());
        if (req.getHeight()!=null)        mb.part("height", req.getHeight().toString());
        if (req.getCfgScale()!=null)      mb.part("cfg_scale", req.getCfgScale().toString());
        if (req.getStylePreset()!=null)   mb.part("style_preset", req.getStylePreset().name().toLowerCase().replace('_','-'));
        mb.part("model", firstNonBlank(req.getModelName(), defaultModel));
        mb.part("control_image", toResource(controlImage))
                .filename(Optional.ofNullable(controlImage.getOriginalFilename()).orElse("pose.png"));

        String accept = StringUtils.hasText(req.getAccept()) ? req.getAccept() : defaultAccept;
        String clientUserId = StringUtils.hasText(userIdForHeader) ? userIdForHeader : "anonymous";

        return stabilityWebClient.post()
                .uri("/v2beta/stable-image/controlnet")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .headers(h -> {
                    h.set(HttpHeaders.ACCEPT, accept);
                    h.set("stability-client-id", appClientId);
                    h.set("stability-client-user-id", clientUserId);
                    h.set("stability-client-version", appClientVersion);
                })
                .body(BodyInserters.fromMultipartData(mb.build()))
                .retrieve()
                .onStatus(HttpStatusCode::isError, r ->
                        r.bodyToMono(String.class).flatMap(b -> Mono.error(new IllegalStateException("Stability error: " + b))))
                .bodyToMono(byte[].class)
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
}
