package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.GeminiResponse;
import com.sep.aiservice.dto.TtsGenerateRequest;
import com.sep.aiservice.dto.AudioResponse;
import com.sep.aiservice.entity.AIGeneration;
import com.sep.aiservice.entity.AIGenerationTarget;
import com.sep.aiservice.entity.Audio;
import com.sep.aiservice.enums.AIGenerationEnum;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
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

            String fileName = "audios/" + gen.getAiGenerationId() + ".wav";
            String publicUrl = storageService.upload(fileName, new ByteArrayInputStream(wav), "audio/wav", wav.length);

            Audio audio = new Audio();
            audio.setAudioUrl(publicUrl);
            audio.setVoice(req.getVoiceName());
            audio.setFormat("wav");
            audio.setLanguage(req.getLanguage());
            audio.setTitle(Optional.ofNullable(req.getTitle()).orElse("tts-" + gen.getAiGenerationId()));
            audio.setDurationMs(durationMs);
            audio.setIsActived(IsActived.ACTIVE);
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
}

