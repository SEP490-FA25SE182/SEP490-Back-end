package com.sep.arservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

@Data
public class AlignmentDataRequest {
    @NotBlank
    @Size(max = 50)
    private String markerId;

    @Size(max = 500)
    private String poseMatrix;        // có thể là chuỗi 16 số (4x4) hoặc JSON

    private Float scale;              // nullable -> không gửi thì bỏ qua

    private Float confidenceScore;

    @Size(max = 100)
    private String rotation;          // ví dụ "rx,ry,rz" hoặc quaternion JSON

    @Size(max = 100)
    private String translation;       // ví dụ "tx,ty,tz"

    private Instant createdAt;
}
