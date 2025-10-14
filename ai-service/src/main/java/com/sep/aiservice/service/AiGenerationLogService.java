package com.sep.aiservice.service;

import com.sep.aiservice.entity.AIGeneration;
import com.sep.aiservice.enums.GenerationMode;
import jakarta.annotation.Nullable;

// Lifecycle
public interface AiGenerationLogService {
    AIGeneration begin(String model, String prompt, GenerationMode mode, String accept, String userId);
    void linkTarget(AIGeneration gen, String targetType, String targetRefId);
    void success(AIGeneration gen, double durationMs);
    void fail(AIGeneration gen, double durationMs, @Nullable Throwable t);
}

