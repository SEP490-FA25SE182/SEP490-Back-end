package com.sep.aiservice.dto;

import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.enums.StylePreset;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class AIGenerationRequest {

    @Size(max = 50)
    private String modelName;

    private String prompt;

    @Size(max = 1000)
    private String negativePrompt;

    @Positive(message = "durationMs must be > 0")
    private Double durationMs;

    private Byte status;

    @Size(max = 50)
    private String userId;

    private GenerationMode mode;

    @Pattern(regexp = "^\\d+:\\d+$", message = "aspectRatio must be like '3:2'")
    @Size(max = 10)
    private String aspectRatio;

    @DecimalMin(value = "0.0", inclusive = false, message = "strength must be > 0")
    @DecimalMax(value = "1.0", message = "strength must be <= 1.0")
    private Double strength;

    @Min(value = 0, message = "seed must be >= 0")
    private Long seed;

    @DecimalMin(value = "0.0", inclusive = false, message = "cfgScale must be > 0")
    private Double cfgScale;

    private StylePreset stylePreset;

    @Pattern(regexp = "^(image/\\*|application/json)$",
            message = "acceptHeader must be 'image/*' or 'application/json'")
    @Size(max = 40)
    private String acceptHeader;

    @Size(max = 500)
    private String inputImageUrl;

    @Size(max = 50) private String style;              // ví dụ "manga-ink"
    @Pattern(regexp="(?i)png|jpg|jpeg|webp") private String format = "png";

    private IsActived isActived;

    private Instant updatedAt;
}
