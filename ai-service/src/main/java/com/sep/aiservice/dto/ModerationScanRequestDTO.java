package com.sep.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ModerationScanRequestDTO {
    @NotBlank
    private String content;

    private String language;
    private String entityType;
    private String entityId;
}