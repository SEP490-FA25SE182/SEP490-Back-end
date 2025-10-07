package com.sep.aiservice.controller;

import com.sep.aiservice.dto.AIGenerationTargetRequest;
import com.sep.aiservice.dto.AIGenerationTargetResponse;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.service.AIGenerationTargetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/ai-generation-targets")
@RequiredArgsConstructor
@Validated
public class AIGenerationTargetController {

    private final AIGenerationTargetService service;

    @GetMapping
    public List<AIGenerationTargetResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AIGenerationTargetResponse getOne(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id
    ) {
        return service.getById(id);
    }

    @PostMapping
    public List<AIGenerationTargetResponse> create(
            @RequestBody @Valid List<AIGenerationTargetRequest> requests
    ) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public AIGenerationTargetResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid AIGenerationTargetRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void softDelete(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id
    ) {
        service.softDelete(id);
    }

    @GetMapping("/search")
    public Page<AIGenerationTargetResponse> search(
            @RequestParam(required = false) @Size(max = 50) String targetType,
            @RequestParam(required = false) @Size(max = 50) String aiGenerationId,
            @RequestParam(required = false) @Size(max = 50) String targetRefId,
            @RequestParam(required = false) IsActived isActived,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(targetType, aiGenerationId, targetRefId, isActived, pageable);
    }
}
