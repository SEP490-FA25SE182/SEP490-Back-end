package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.AIGenerationTargetRequest;
import com.sep.aiservice.dto.AIGenerationTargetResponse;
import com.sep.aiservice.entity.AIGenerationTarget;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.mapper.AIGenerationTargetMapper;
import com.sep.aiservice.repository.AIGenerationTargetRepository;
import com.sep.aiservice.service.AIGenerationTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AIGenerationTargetServiceImpl implements AIGenerationTargetService {

    private final AIGenerationTargetRepository repository;
    private final AIGenerationTargetMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allAIGenerationTargets", key = "'all'")
    public List<AIGenerationTargetResponse> getAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "AIGenerationTarget", key = "#id")
    public AIGenerationTargetResponse getById(String id) {
        AIGenerationTarget e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIGenerationTarget not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allAIGenerationTargets", "AIGenerationTarget"}, allEntries = true)
    public List<AIGenerationTargetResponse> create(List<AIGenerationTargetRequest> requests) {
        List<AIGenerationTarget> entities = requests.stream().map(req -> {
            if (req.getAiGenerationId() == null || req.getAiGenerationId().isBlank())
                throw new IllegalArgumentException("aiGenerationId is required");

            AIGenerationTarget e = new AIGenerationTarget();
            mapper.copyForCreate(req, e);

            if (e.getIsActived() == null) e.setIsActived(IsActived.ACTIVE);
            if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());
            e.setUpdatedAt(Instant.now());

            return e;
        }).toList();

        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allAIGenerationTargets", "AIGenerationTarget"}, allEntries = true)
    public AIGenerationTargetResponse update(String id, AIGenerationTargetRequest request) {
        AIGenerationTarget e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIGenerationTarget not found: " + id));

        mapper.copyForUpdate(request, e);
        e.setUpdatedAt(Instant.now());

        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allAIGenerationTargets", "AIGenerationTarget"}, allEntries = true)
    public void softDelete(String id) {
        AIGenerationTarget e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIGenerationTarget not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        e.setUpdatedAt(Instant.now());
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AIGenerationTargetResponse> search(
            String targetType,
            String aiGenerationId,
            String targetRefId,
            IsActived isActived,
            Pageable pageable
    ) {
        String tt = normalize(targetType);
        String aid = normalize(aiGenerationId);
        String tr = normalize(targetRefId);

        AIGenerationTarget probe = new AIGenerationTarget();
        if (tt != null) probe.setTargetType(tt);
        if (aid != null) probe.setAiGenerationId(aid);
        if (tr != null) probe.setTargetRefId(tr);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("targetType", m -> m.ignoreCase())
                .withMatcher("aiGenerationId", m -> m.ignoreCase())
                .withMatcher("targetRefId", m -> m.ignoreCase())
                .withIgnorePaths(
                        "aiGenerationTargetId",
                        "createdAt",
                        "updatedAt",
                        "aiGeneration" // quan hệ
                )
                .withIgnoreNullValues();

        Example<AIGenerationTarget> example = Example.of(probe, matcher);

        return repository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
