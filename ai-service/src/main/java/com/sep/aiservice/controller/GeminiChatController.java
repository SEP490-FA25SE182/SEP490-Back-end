package com.sep.aiservice.controller;

import com.sep.aiservice.dto.ChatRequestDTO;
import com.sep.aiservice.dto.ChatResponseDTO;
import com.sep.aiservice.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/chat")
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
}
