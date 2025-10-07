package com.sep.aiservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AIGenerationTargetResponse {
    private String aiGenerationTargetId;
    private String targetType;
    private Instant createdAt;
    private Instant updatedAt;
    private String aiGenerationId;
    private String targetRefId;
}
