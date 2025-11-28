package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asset3DRequest {

    @NotBlank
    private String assetUrl;

    @NotBlank
    private String fileName;

    @NotBlank
    private String markerId;

    private String thumbUrl;
    private String prompt;
    private Float scale;
    private String userId;
}

