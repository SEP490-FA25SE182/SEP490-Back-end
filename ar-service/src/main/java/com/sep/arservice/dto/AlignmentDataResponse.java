package com.sep.arservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AlignmentDataResponse {
    private String alignmentDataId;
    private String markerId;
    private String poseMatrix;
    private float  scale;
    private float  confidenceScore;
    private String rotation;
    private String translation;
    private Instant createdAt;
    private Instant updatedAt;
}
