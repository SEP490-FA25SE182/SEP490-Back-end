package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatResponseDTO {
    private String sessionId;
    private String content;
    private Instant createdAt;
    private String role;
}
