package com.sep.aiservice.service.impl;

import com.sep.aiservice.dto.AIGenerationRequest;
import com.sep.aiservice.dto.AIGenerationResponse;
import com.sep.aiservice.entity.AIGeneration;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.mapper.AIGenerationMapper;
import com.sep.aiservice.repository.AIGenerationRepository;
import com.sep.aiservice.service.AIGenerationService;
import lombok.RequiredArgsConstructor;
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
public class AIGenerationServiceImpl implements AIGenerationService {

    private final AIGenerationRepository repository;
    private final AIGenerationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allAIGenerations", key = "'all'")
    public List<AIGenerationResponse> getAll() {
        return repository.findAll()
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "AIGeneration", key = "#id")
    public AIGenerationResponse getById(String id) {
        AIGeneration e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIGeneration not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @CacheEvict(value = {"allAIGenerations", "AIGeneration"}, allEntries = true)
    public List<AIGenerationResponse> create(List<AIGenerationRequest> requests) {
        List<AIGeneration> entities = requests.stream().map(req -> {
            if (req.getModelName() == null || req.getModelName().isBlank())
                throw new IllegalArgumentException("modelName is required");
            // prompt có thể null nếu là img2img (mode khác TEXT_TO_IMAGE)
            if (req.getDurationMs() != null && req.getDurationMs() <= 0)
                throw new IllegalArgumentException("durationMs must be > 0");

            AIGeneration e = new AIGeneration();
            mapper.copyForCreate(req, e);

            if (e.getIsActived() == null) e.setIsActived(IsActived.ACTIVE);
            if (e.getCreatedAt() == null) e.setCreatedAt(Instant.now());

            return e;
        }).toList();

        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allAIGenerations", "AIGeneration"}, allEntries = true)
    public AIGenerationResponse update(String id, AIGenerationRequest request) {
        AIGeneration e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIGeneration not found: " + id));

        mapper.copyForUpdate(request, e);

        return mapper.toResponse(repository.save(e));
    }

    @Override
    @CacheEvict(value = {"allAIGenerations", "AIGeneration"}, allEntries = true)
    public void softDelete(String id) {
        AIGeneration e = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("AIGeneration not found: " + id));
        e.setIsActived(IsActived.INACTIVE);
        repository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AIGenerationResponse> search(
            String modelName,
            String userId,
            com.sep.aiservice.enums.GenerationMode mode,
            Byte status,
            com.sep.aiservice.enums.StylePreset stylePreset,
            String promptContains,
            IsActived isActived,
            Pageable pageable
    ) {
        String m = normalize(modelName);
        String u = normalize(userId);
        String pc = normalize(promptContains);

        AIGeneration probe = new AIGeneration();
        if (m != null) probe.setModelName(m);
        if (u != null) probe.setUserId(u);
        if (mode != null) probe.setMode(mode);
        if (status != null) probe.setStatus(status);
        if (stylePreset != null) probe.setStylePreset(stylePreset);
        if (isActived != null) probe.setIsActived(isActived);

        ExampleMatcher matcher = ExampleMatcher.matchingAll()
                .withMatcher("modelName", mm -> mm.ignoreCase())
                .withMatcher("userId", mm -> mm.ignoreCase())
                .withMatcher("prompt", mm -> mm.ignoreCase().contains())
                .withIgnorePaths(
                        "aiGenerationId",
                        "negativePrompt",
                        "durationMs",
                        "createdAt",
                        "aspectRatio",
                        "strength",
                        "seed",
                        "cfgScale",
                        "acceptHeader",
                        "inputImageUrl",
                        "aiGenerationTargets"
                )
                .withIgnoreNullValues();

        if (pc != null) probe.setPrompt(pc);

        Example<AIGeneration> example = Example.of(probe, matcher);

        return repository.findAll(example, pageable)
                .map(mapper::toResponse);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
