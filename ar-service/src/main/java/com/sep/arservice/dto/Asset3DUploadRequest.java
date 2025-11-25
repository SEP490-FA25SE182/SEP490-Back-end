package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Asset3DUploadRequest {
    @NotBlank
    @Size(max=50)
    private String markerId;

    @Size(max=50)
    private String userId;

    @Size(max=500)
    private String prompt;

    @Size(max = 200)
    private String fileName;

    @Size(max=10)
    private String format; // GLB/FBX

    private Float scale; // optional
}
