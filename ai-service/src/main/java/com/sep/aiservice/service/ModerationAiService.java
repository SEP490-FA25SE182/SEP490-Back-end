package com.sep.aiservice.service;

import com.sep.aiservice.dto.AiModerationResultDTO;

public interface ModerationAiService {
    AiModerationResultDTO analyze(String content, int forbiddenCount, double maxSimilarity);
}