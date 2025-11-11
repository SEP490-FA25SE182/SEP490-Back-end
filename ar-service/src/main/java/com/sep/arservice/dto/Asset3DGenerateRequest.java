package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Asset3DGenerateRequest {
    @NotBlank
    @Size(max=50)
    private String markerId;

    @NotBlank
    @Size(max=50)
    private String userId;

    @Size(max=500)
    private String prompt;

    @Size(max = 200)
    private String fileName;

    @Size(max=10)
    private String format; // GLB (khuyến nghị cho Web/Unity)

    @Size(max=30)
    private String quality; // balanced/high…
}
