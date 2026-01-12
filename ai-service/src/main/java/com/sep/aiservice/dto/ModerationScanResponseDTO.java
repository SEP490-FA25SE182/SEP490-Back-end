package com.sep.aiservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ModerationScanResponseDTO {

    private String language;

    private int forbiddenCount;
    @Builder.Default
    private List<ForbiddenWordMatchDTO> forbiddenMatches = new ArrayList<>();

    private double maxSimilarity;
    private boolean plagiarismFlag;
    @Builder.Default
    private List<PlagiarismHitDTO> plagiarismHits = new ArrayList<>();

    private String aiRiskLevel;
    private String aiAction;
    @Builder.Default
    private List<String> aiReasons = new ArrayList<>();
}