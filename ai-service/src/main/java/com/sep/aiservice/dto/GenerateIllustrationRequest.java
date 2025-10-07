package com.sep.aiservice.dto;

import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.StylePreset;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GenerateIllustrationRequest {
    @Size(max = 50)
    private String modelName;

    @NotBlank
    @Size(max = 500)
    private String prompt;

    @Size(max = 500)
    private String negativePrompt;

    @Size(max = 50)
    private String style;              // ví dụ "manga-ink"

    @Pattern(regexp="(?i)png|jpg|jpeg|webp")
    private String format = "png";

    @Positive
    @Max(2048)
    private Integer width = 1024;

    @Positive @Max(2048)
    private Integer height = 1024;

    @Size(max = 50)
    private String title;

    @Size(max = 20)
    private String controlnetType;     // "openpose" | "canny" | "depth" ...

    private Long seed;

    private GenerationMode mode;

    @Pattern(regexp = "^\\d+:\\d+$", message = "aspectRatio must be like '3:2'")
    @Size(max = 10)
    private String aspectRatio;

    @DecimalMin(value = "0.0", inclusive = false, message = "strength must be > 0")
    @DecimalMax(value = "1.0", message = "strength must be <= 1.0")
    private Double strength;

    @DecimalMin(value = "0.0", inclusive = false, message = "cfgScale must be > 0")
    private Double cfgScale;

    private StylePreset stylePreset;

    @Pattern(regexp = "^(image/\\*|application/json)$",
            message = "acceptHeader must be 'image/*' or 'application/json'")
    @Size(max = 40)
    private String accept;

}

