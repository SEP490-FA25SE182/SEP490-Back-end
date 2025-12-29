package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatResponseDTO {
    private String sessionId;
    private String message;
    private String answer;
    private Instant createdAt;
    private String role;
}
