package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.AudioUploadRequest;
import com.sep.aiservice.dto.GeminiResponse;
import com.sep.aiservice.dto.TtsGenerateRequest;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.entity.AIGeneration;
import jakarta.persistence.*;
import com.sep.aiservice.entity.Audio;
import java.util.UUID;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.mapper.AudioMapper;
import com.sep.aiservice.repository.AIGenerationRepository;
import com.sep.aiservice.repository.AIGenerationTargetRepository;
import com.sep.aiservice.repository.AudioRepository;
import com.sep.aiservice.service.AiGenerationLogService;
import com.sep.aiservice.service.StorageService;
import com.sep.aiservice.service.TextToSpeechService;
import com.sep.aiservice.util.PcmToWavConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextToSpeechServiceImpl implements TextToSpeechService {

    @Qualifier("geminiWebClient")
    private final WebClient geminiClient;

    private final AiGenerationLogService genLog;
    private final AudioRepository audioRepo;
    private final AudioMapper audioMapper;
    private final StorageService storageService;

    @Override
    @Transactional
    public AudioResponse synthesize(TtsGenerateRequest req, String userId) {
        long t0 = System.nanoTime();
        AIGeneration gen = genLog.begin(
                req.getModel(),
                req.getText(),
                GenerationMode.TEXT_TO_SPEECH,
                "application/json",
                userId
        );

        try {
            String endpoint = String.format("/v1beta/models/%s:generateContent", req.getModel());
            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", req.getText())))),
                    "generationConfig", Map.of(
                            "responseModalities", List.of("AUDIO"),
                            "speechConfig", Map.of(
                                    "voiceConfig", Map.of(
                                            "prebuiltVoiceConfig", Map.of("voiceName", req.getVoiceName())
                                    )
                            )
                    )
            );

            GeminiResponse response = geminiClient.post()
                    .uri(endpoint)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block();

            // validate
            String b64 = Optional.ofNullable(response)
                    .map(GeminiResponse::getCandidates).filter(l -> !l.isEmpty())
                    .map(l -> l.get(0).getContent()).map(GeminiResponse.Content::getParts).filter(l -> !l.isEmpty())
                    .map(l -> l.get(0).getInlineData()).map(GeminiResponse.InlineData::getData)
                    .orElseThrow(() -> new IllegalStateException("Gemini response missing audio data"));

            byte[] pcm = Base64.getDecoder().decode(b64);

            // PCM -> WAV mono 16-bit 24kHz

            int sampleRate = 24000;
            byte[] wav = PcmToWavConverter.toWavMono16LE(pcm, sampleRate);

            int bytesPerSample = 2;
            double durationMs = (pcm.length / (double) bytesPerSample) / sampleRate * 1000.0;

            String ext = "wav";
            String baseTitle = Optional.ofNullable(req.getTitle())
                    .filter(org.springframework.util.StringUtils::hasText)
                    .orElse("tts");
            String safe = slugify(baseTitle);
            String shortId = gen.getAiGenerationId().substring(0, 8);
            String fileName = String.format("audios/%s-%s.%s", safe, shortId, ext);

            String publicUrl = storageService.upload(
                    fileName,
                    new ByteArrayInputStream(wav),
                    "audio/wav",
                    wav.length
            );

            Audio audio = new Audio();
            audio.setAudioUrl(publicUrl);
            audio.setVoice(req.getVoiceName());
            audio.setFormat("wav");
            audio.setLanguage(req.getLanguage());
            audio.setTitle(Optional.ofNullable(req.getTitle()).orElse("tts-" + gen.getAiGenerationId()));
            audio.setDurationMs(durationMs);
            audio.setIsActived(IsActived.ACTIVE);
            audio.setUserId(userId);
            audio = audioRepo.save(audio);

            genLog.linkTarget(gen, "AUDIO", audio.getAudioId());
            genLog.success(gen, (System.nanoTime() - t0) / 1_000_000.0);

            return audioMapper.toResponse(audio);

        } catch (Exception e) {
            log.error("TTS generation failed", e);
            genLog.fail(gen, (System.nanoTime() - t0) / 1_000_000.0, e);
            throw new RuntimeException("TTS generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public AudioResponse uploadAudio(MultipartFile file, AudioUploadRequest meta, String userId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File audio is empty");
        }

        try {
            // 1. Lấy tên file gốc & đuôi file (format)
            String originalName = file.getOriginalFilename();
            if (!StringUtils.hasText(originalName)) {
                originalName = "audio";
            }

            String ext = "";
            int dot = originalName.lastIndexOf('.');
            if (dot >= 0 && dot < originalName.length() - 1) {
                ext = originalName.substring(dot + 1).toLowerCase();
            }

            // Nếu không có extension thì default thành "wav"
            if (!StringUtils.hasText(ext)) {
                ext = "wav";
            }

            // Validate format theo list cho phép
            if (!ext.matches("(?i)mp3|wav|ogg|m4a|flac")) {
                throw new IllegalArgumentException("Unsupported audio format: " + ext);
            }

            // 2. Title để lưu DB & đặt tên file
            String finalTitle = (meta != null && StringUtils.hasText(meta.getTitle()))
                    ? meta.getTitle()
                    : originalName;

            // 3. Language (optional)
            String language = (meta != null) ? meta.getLanguage() : null;

            // 4. Tạo tên file an toàn cho Firebase
            String safe = slugify(finalTitle);
            String shortId = UUID.randomUUID().toString().substring(0, 8);
            String fileName = String.format("audios/%s-%s.%s", safe, shortId, ext);

            // 5. ContentType
            String contentType = file.getContentType();
            if (!StringUtils.hasText(contentType)) {
                // đoán đơn giản cho audio
                contentType = "audio/" + ext;
            }

            // 6. Upload Firebase
            String publicUrl = storageService.upload(
                    fileName,
                    file.getInputStream(),
                    contentType,
                    file.getSize()
            );

            // 7. Lưu Audio vào DB
            Audio audio = new Audio();
            audio.setAudioUrl(publicUrl);
            audio.setVoice(null);      // upload file có sẵn nên không có voiceName
            audio.setFormat(ext);
            audio.setLanguage(language);
            audio.setTitle(finalTitle);
            audio.setDurationMs(0);    // nếu cần tính duration thì thêm lib sau
            audio.setIsActived(IsActived.ACTIVE);
            audio.setUserId(userId);

            audio = audioRepo.save(audio);

            // 8. Trả về response
            return audioMapper.toResponse(audio);

        } catch (IOException e) {
            log.error("Upload audio failed", e);
            throw new RuntimeException("Upload audio failed: " + e.getMessage(), e);
        }
    }

    private static String slugify(String input) {
        String noDiacritics = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String cleaned = noDiacritics
                .replaceAll("[^A-Za-z0-9\\-_. ]", " ") // loại ký tự lạ
                .trim()
                .replaceAll("\\s+", "-")               // space -> '-'
                .replaceAll("-{2,}", "-")              // gộp nhiều '-' liên tiếp
                .toLowerCase();
        if (cleaned.length() > 80) cleaned = cleaned.substring(0, 80);
        return cleaned.isEmpty() ? "tts" : cleaned;
    }

}
