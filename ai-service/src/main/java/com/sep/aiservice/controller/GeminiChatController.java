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
            @RequestHeader("X-User-Id") String userId,   // giống TTS
            @RequestBody ChatRequestDTO req
    ) {
        return ResponseEntity.ok(service.chat(req, userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatResponseDTO>> getHistory(@RequestHeader("X-User-Id") String userId) {
        List<ChatMessage> history = service.getUserChatHistory(userId);

        List<ChatResponseDTO> dtos = history.stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(msg -> {
                    ChatResponseDTO userDto = ChatResponseDTO.builder()
                            .sessionId(msg.getSessionId())
                            .message(msg.getUserMessage())
                            .createdAt(msg.getCreatedAt())
                            .role("user")
                            .build();

                    ChatResponseDTO aiDto = ChatResponseDTO.builder()
                            .sessionId(msg.getSessionId())
                            .answer(msg.getAiResponse())
                            .createdAt(msg.getCreatedAt())
                            .role("model")
                            .build();

                    return List.of(userDto, aiDto);
                })
                .flatMap(List::stream)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}
