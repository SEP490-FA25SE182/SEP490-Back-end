package com.sep.aiservice.service;

import com.sep.aiservice.dto.ChatRequestDTO;
import com.sep.aiservice.dto.ChatResponseDTO;
import com.sep.aiservice.entity.ChatMessage;

import java.util.List;

public interface GeminiChatService {
    ChatResponseDTO chat(ChatRequestDTO request, String userId);
    List<ChatMessage> getUserChatHistory(String userId);
}
