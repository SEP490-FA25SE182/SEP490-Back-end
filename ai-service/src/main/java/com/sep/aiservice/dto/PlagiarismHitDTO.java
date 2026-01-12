package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlagiarismHitDTO {
    private String sourceType;
    private String sourceId;
    private double similarity;
    private String snippet;
}