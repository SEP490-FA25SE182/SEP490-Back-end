package com.sep.aiservice.service.impl;

import com.sep.aiservice.entity.AIGeneration;
import com.sep.aiservice.entity.AIGenerationTarget;
import com.sep.aiservice.enums.AIGenerationEnum;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.repository.AIGenerationRepository;
import com.sep.aiservice.repository.AIGenerationTargetRepository;
import com.sep.aiservice.service.AiGenerationLogService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiGenerationLogServiceImpl implements AiGenerationLogService {

    private final AIGenerationRepository aiGenRepo;
    private final AIGenerationTargetRepository aiTargetRepo;

    @Override
    @Transactional
    public AIGeneration begin(String model, String prompt, GenerationMode mode, String accept, String userId) {
        AIGeneration gen = new AIGeneration();
        gen.setModelName(model);
        gen.setPrompt(prompt);
        gen.setMode(mode);
        gen.setStatus(AIGenerationEnum.PENDING.getStatus());
        gen.setAcceptHeader(accept);
        gen.setUserId(userId);
        gen.setIsActived(IsActived.ACTIVE);
        return aiGenRepo.save(gen);
    }

    @Override
    @Transactional
    public void linkTarget(AIGeneration gen, String targetType, String targetRefId) {
        AIGenerationTarget tgt = new AIGenerationTarget();
        tgt.setAiGenerationId(gen.getAiGenerationId());
        tgt.setTargetType(targetType);
        tgt.setTargetRefId(targetRefId);
        tgt.setIsActived(IsActived.ACTIVE);
        aiTargetRepo.save(tgt);
    }

    @Override
    @Transactional
    public void success(AIGeneration gen, double durationMs) {
        gen.setDurationMs(durationMs);
        gen.setStatus(AIGenerationEnum.SUCCESS.getStatus());
        aiGenRepo.save(gen);
    }

    @Override
    @Transactional
    public void fail(AIGeneration gen, double durationMs, @Nullable Throwable t) {
        gen.setDurationMs(durationMs);
        gen.setStatus(AIGenerationEnum.FAILED.getStatus());
        aiGenRepo.save(gen);
    }
}

