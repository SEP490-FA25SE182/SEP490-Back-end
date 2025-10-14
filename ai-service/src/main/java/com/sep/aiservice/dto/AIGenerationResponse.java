package com.sep.aiservice.dto;

import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.enums.StylePreset;
import lombok.Data;

import java.time.Instant;

@Data
public class AIGenerationResponse {
    private String aiGenerationId;
    private String modelName;
    private IsActived isActived;

    // request params
    private String prompt;
    private String negativePrompt;
    private GenerationMode mode;
    private String aspectRatio;
    private Double strength;
    private Long seed;
    private Double cfgScale;
    private StylePreset stylePreset;
    private String acceptHeader;
    private String inputImageUrl;

    // audit
    private Double durationMs;
    private Byte status;
    private Instant createdAt;
    private Instant updatedAt;
    private String userId;
}