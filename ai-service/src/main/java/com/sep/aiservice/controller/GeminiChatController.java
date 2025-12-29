package com.sep.aiservice.controller;

import com.sep.aiservice.dto.ChatRequestDTO;
import com.sep.aiservice.dto.ChatResponseDTO;
import com.sep.aiservice.entity.ChatMessage;
import com.sep.aiservice.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/chat")
@RequiredArgsConstructor
public class GeminiChatController {

    private final GeminiChatService service;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ChatRequestDTO req
    ) {
        return ResponseEntity.ok(service.chat(req, userId));
    }

    /**
     * Lấy toàn bộ lịch sử chat của user (tất cả session)
     * Trả về danh sách tin nhắn luân phiên user → model
     * Sắp xếp theo thời gian tăng dần (cũ → mới)
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatResponseDTO>> getHistory(@RequestHeader("X-User-Id") String userId) {
        List<ChatMessage> history = service.getUserChatHistory(userId);

        List<ChatResponseDTO> dtos = history.stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(msg -> ChatResponseDTO.builder()
                        .sessionId(msg.getSessionId())
                        .content(msg.getContent())
                        .role(msg.getRole())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(dtos);
    }
}