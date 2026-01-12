package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AiModerationResultDTO {
    private String riskLevel; // LOW/MEDIUM/HIGH/UNKNOWN
    private String action;    // APPROVE/REVIEW/REJECT/UNKNOWN
    @Builder.Default
    private List<String> reasons = new ArrayList<>();
}