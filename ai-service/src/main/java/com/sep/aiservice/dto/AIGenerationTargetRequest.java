package com.sep.aiservice.dto;

import com.sep.aiservice.enums.IsActived;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class AIGenerationTargetRequest {

    @Size(max = 50)
    private String targetType;

    @NotBlank(message = "aiGenerationId is required")
    @Size(max = 50)
    private String aiGenerationId;

    @Size(max = 50)
    private String targetRefId;

    private IsActived isActived;

    private Instant updatedAt;
}
