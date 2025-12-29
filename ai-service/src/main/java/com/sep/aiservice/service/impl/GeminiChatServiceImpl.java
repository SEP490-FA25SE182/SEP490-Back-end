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
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiChatServiceImpl implements GeminiChatService {

    @Qualifier("geminiWebClient")
    private final WebClient geminiClient;

    private final ChatMessageRepository chatRepo;
    private final AiGenerationLogService genLog;

    private static final String MODEL_ENDPOINT = "/v1beta/models/gemini-2.0-flash:generateContent";

    // HttpClient để tải ảnh/file từ URL → base64 (reactive + non-blocking)
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    @Transactional
    public ChatResponseDTO chat(ChatRequestDTO req, String userId) {
        long t0 = System.nanoTime();

        String sessionId = Optional.ofNullable(req.getSessionId())
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());

        var gen = genLog.begin(
                "gemini-2.0-flash",
                req.getMessage() != null ? req.getMessage() : "multimodal input",
                GenerationMode.CHAT,
                "application/json",
                userId
        );

        try {
            // Load lịch sử theo session (chỉ để gửi Gemini)
            List<ChatMessage> history = chatRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);

            List<Map<String, Object>> contents = new ArrayList<>();

            // Thêm lịch sử text (ảnh cũ không gửi lại)
            for (ChatMessage m : history) {
                if ("user".equals(m.getRole())) {
                    contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", m.getContent()))));
                } else if ("model".equals(m.getRole())) {
                    contents.add(Map.of("role", "model", "parts", List.of(Map.of("text", m.getContent()))));
                }
            }

            // Tin nhắn hiện tại
            List<Map<String, Object>> currentParts = new ArrayList<>();

            String userMessageText = req.getMessage() != null && !req.getMessage().isBlank() ? req.getMessage() : null;

            if (userMessageText != null) {
                currentParts.add(Map.of("text", userMessageText));
            }

            // Ảnh
            if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
                for (String imageUrl : req.getImageUrls()) {
                    String base64 = urlToBase64(imageUrl);
                    String mimeType = guessMimeType(imageUrl);
                    currentParts.add(Map.of(
                            "inlineData", Map.of("mimeType", mimeType, "data", base64)
                    ));
                }
            }

            // File PDF/DOC
            if (req.getFileUrls() != null && !req.getFileUrls().isEmpty()) {
                for (String fileUrl : req.getFileUrls()) {
                    currentParts.add(Map.of(
                            "fileData", Map.of("mimeType", guessMimeType(fileUrl), "fileUri", fileUrl)
                    ));
                }
            }

            if (currentParts.isEmpty()) {
                throw new IllegalArgumentException("Tin nhắn phải có nội dung");
            }

            contents.add(Map.of("role", "user", "parts", currentParts));

            Map<String, Object> body = Map.of("contents", contents);

            log.info("Gửi Gemini multimodal – sessionId={}, userId={}, images={}, files={}",
                    sessionId, userId,
                    req.getImageUrls() != null ? req.getImageUrls().size() : 0,
                    req.getFileUrls() != null ? req.getFileUrls().size() : 0);

            @SuppressWarnings("unchecked")
            Map<String, Object> result = geminiClient.post()
                    .uri(MODEL_ENDPOINT)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result == null) {
                throw new RuntimeException("Gemini trả về null");
            }

            String answer = extractAnswer(result);

            Instant now = Instant.now();

            // LƯU 2 DÒNG RIÊNG BIỆT
            // 1. Tin nhắn user
            String userContent = userMessageText != null ? userMessageText :
                    "Đã gửi " +
                            (req.getImageUrls() != null ? req.getImageUrls().size() : 0) + " ảnh " +
                            (req.getFileUrls() != null ? req.getFileUrls().size() : 0) + " file";

            chatRepo.save(ChatMessage.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .role("user")
                    .content(userContent)
                    .createdAt(now.minusSeconds(1))  // để user trước AI
                    .build());

            // 2. Phản hồi AI
            chatRepo.save(ChatMessage.builder()
                    .sessionId(sessionId)
                    .userId(userId)
                    .role("model")
                    .content(answer)
                    .createdAt(now)
                    .build());

            genLog.success(gen, (System.nanoTime() - t0) / 1_000_000.0);

            return ChatResponseDTO.builder()
                    .sessionId(sessionId)
                    .content(answer)
                    .build();

        } catch (Exception e) {
            log.error("Gemini chat error for userId={}, sessionId={}", userId, sessionId, e);
            genLog.fail(gen, (System.nanoTime() - t0) / 1_000_000.0, e);
            throw new RuntimeException("Gemini chat failed: " + e.getMessage(), e);
        }
    }

    // Tải URL → base64 (ảnh)
    private String urlToBase64(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Không tải được ảnh: HTTP " + response.statusCode());
            }

            return Base64.getEncoder().encodeToString(response.body());
        } catch (Exception e) {
            log.warn("Lỗi tải ảnh từ URL: {}", url, e);
            throw new RuntimeException("Không thể xử lý ảnh từ URL: " + url);
        }
    }

    // Đoán MIME type
    private String guessMimeType(String url) {
        if (url == null) return "application/octet-stream";
        String clean = url.split("\\?")[0].toLowerCase();

        if (clean.endsWith(".png")) return "image/png";
        if (clean.endsWith(".jpg") || clean.endsWith(".jpeg")) return "image/jpeg";
        if (clean.endsWith(".gif")) return "image/gif";
        if (clean.endsWith(".webp")) return "image/webp";
        if (clean.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }

    // Extract answer từ response
    @SuppressWarnings("unchecked")
    private String extractAnswer(Map<String, Object> json) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) json.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Gemini không trả về phản hồi.";
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return "Gemini trả về phản hồi rỗng.";
            }

            StringBuilder answer = new StringBuilder();
            for (Map<String, Object> part : parts) {
                if (part.containsKey("text")) {
                    answer.append(part.get("text")).append("\n");
                }
            }
            return answer.length() > 0 ? answer.toString().trim() : "Không thể đọc phản hồi từ Gemini.";
        } catch (Exception e) {
            log.warn("Lỗi extract answer từ Gemini response", e);
            return "Không thể đọc phản hồi từ Gemini.";
        }
    }

    @Override
    public List<ChatMessage> getUserChatHistory(String userId) {
        return chatRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}