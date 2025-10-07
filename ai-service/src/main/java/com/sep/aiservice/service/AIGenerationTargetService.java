package com.sep.aiservice.service;

import com.sep.aiservice.dto.AIGenerationTargetRequest;
import com.sep.aiservice.dto.AIGenerationTargetResponse;
import com.sep.aiservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AIGenerationTargetService {
    List<AIGenerationTargetResponse> getAll();
    AIGenerationTargetResponse getById(String id);
    List<AIGenerationTargetResponse> create(List<AIGenerationTargetRequest> requests);
    AIGenerationTargetResponse update(String id, AIGenerationTargetRequest request);
    void softDelete(String id);

    Page<AIGenerationTargetResponse> search(
            String targetType,
            String aiGenerationId,
            String targetRefId,
            IsActived isActived,
            Pageable pageable
    );
}
