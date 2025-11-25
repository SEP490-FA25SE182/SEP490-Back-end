package com.sep.aiservice.service;

import com.sep.aiservice.dto.ChatRequestDTO;
import com.sep.aiservice.dto.ChatResponseDTO;

public interface GeminiChatService {
    ChatResponseDTO chat(ChatRequestDTO request, String userId);
}
