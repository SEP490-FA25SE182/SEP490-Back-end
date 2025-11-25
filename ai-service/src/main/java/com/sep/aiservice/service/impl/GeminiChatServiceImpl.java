package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.ChatRequestDTO;
import com.sep.aiservice.dto.ChatResponseDTO;
import com.sep.aiservice.entity.ChatMessage;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.repository.ChatMessageRepository;
import com.sep.aiservice.service.AiGenerationLogService;
import com.sep.aiservice.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiChatServiceImpl implements GeminiChatService {

    @Qualifier("geminiWebClient")
    private final WebClient geminiClient;

    private final ChatMessageRepository chatRepo;
    private final AiGenerationLogService genLog;

    private static final String MODEL_ENDPOINT =
            "/v1beta/models/gemini-1.5-flash:generateContent";

    @Override
    @Transactional
    public ChatResponseDTO chat(ChatRequestDTO req, String userId) {
        long t0 = System.nanoTime();

        String sessionId = (req.getSessionId() != null && !req.getSessionId().isBlank())
                ? req.getSessionId()
                : UUID.randomUUID().toString();

        var gen = genLog.begin(
                "gemini-1.5-flash",
                req.getMessage(),
                GenerationMode.CHAT,
                "application/json",
                userId
        );

        try {
            var history = chatRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);

            List<Map<String, Object>> messages = new ArrayList<>();

            for (ChatMessage m : history) {
                messages.add(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", m.getUserMessage()))
                ));
                messages.add(Map.of(
                        "role", "model",
                        "parts", List.of(Map.of("text", m.getAiResponse()))
                ));
            }

            messages.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", req.getMessage()))
            ));

            Map<String, Object> body = Map.of(
                    "contents", messages
            );

            Map result = geminiClient.post()
                    .uri(MODEL_ENDPOINT)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String answer = extractAnswer(result);

            chatRepo.save(ChatMessage.builder()
                    .sessionId(sessionId)
                    .userMessage(req.getMessage())
                    .aiResponse(answer)
                    .build());

            genLog.success(gen, (System.nanoTime() - t0) / 1_000_000.0);

            return ChatResponseDTO.builder()
                    .sessionId(sessionId)
                    .answer(answer)
                    .build();

        } catch (Exception e) {
            log.error("Gemini chat error", e);
            genLog.fail(gen, (System.nanoTime() - t0) / 1_000_000.0, e);
            throw new RuntimeException("Gemini chat failed: " + e.getMessage(), e);
        }
    }

    private String extractAnswer(Map json) {
        try {
            return (String) ((Map)((Map)((List)json.get("candidates")).get(0))
                    .get("content"))
                    .get("parts").toString();
        } catch (Exception e) {
            return "Không thể đọc phản hồi từ Gemini.";
        }
    }
}
