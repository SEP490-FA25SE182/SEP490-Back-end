package com.sep.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PageIllustrationRequest {
    @Size(max = 50)
    @NotBlank
    private String pageId;

    @Size(max = 50)
    @NotBlank
    private String illustrationId;
}
