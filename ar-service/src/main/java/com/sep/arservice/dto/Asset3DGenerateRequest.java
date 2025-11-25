package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Asset3DGenerateRequest {
    @NotBlank
    @Size(max=50)
    String markerId;

    @NotBlank
    @Size(max=50)
    String userId;

    @Size(max=500)
    String prompt;

    @Size(max=10)
    String format;   // GLB/FBX/OBJ

    @Size(max=30)
    String quality;  // balanced/high…

    @Size(max=200)
    String fileName; // bạn đã có

    // Dành cho refine/texturing
    Boolean refine;
    Boolean enablePbr;              // default true
    @Size(max=600)
    String texturePrompt;   // optional, gợi ý texturing

    @Size(max=1000)
    String textureImageUrl; // optional, nếu muốn texture theo ảnh
}

