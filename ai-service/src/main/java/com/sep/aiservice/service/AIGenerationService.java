package com.sep.aiservice.service;

import com.sep.aiservice.dto.AIGenerationRequest;
import com.sep.aiservice.dto.AIGenerationResponse;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.enums.StylePreset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AIGenerationService {
    List<AIGenerationResponse> getAll();
    AIGenerationResponse getById(String id);
    List<AIGenerationResponse> create(List<AIGenerationRequest> requests);
    AIGenerationResponse update(String id, AIGenerationRequest request);
    void softDelete(String id);
    Page<AIGenerationResponse> search(
            String modelName,
            String userId,
            GenerationMode mode,
            Byte status,
            StylePreset stylePreset,
            String promptContains,
            IsActived isActived,
            Pageable pageable
    );
}
