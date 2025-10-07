package com.sep.aiservice.controller;

import com.sep.aiservice.dto.AIGenerationRequest;
import com.sep.aiservice.dto.AIGenerationResponse;
import com.sep.aiservice.enums.GenerationMode;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.enums.StylePreset;
import com.sep.aiservice.service.AIGenerationService;
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
@RequestMapping("/api/rookie/ai-generations")
@RequiredArgsConstructor
@Validated
public class AIGenerationController {

    private final AIGenerationService service;

    @GetMapping
    public List<AIGenerationResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AIGenerationResponse getOne(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id
    ) {
        return service.getById(id);
    }

    @PostMapping
    public List<AIGenerationResponse> create(@RequestBody @Valid List<AIGenerationRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public AIGenerationResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid AIGenerationRequest request
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
    public Page<AIGenerationResponse> search(
            @RequestParam(required = false) @Size(max = 50) String modelName,
            @RequestParam(required = false) @Size(max = 50) String userId,
            @RequestParam(required = false) GenerationMode mode,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) StylePreset stylePreset,
            @RequestParam(required = false) @Size(max = 1000) String promptContains,
            @RequestParam(required = false) IsActived isActived,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(modelName, userId, mode, status, stylePreset, promptContains, isActived, pageable);
    }
}
