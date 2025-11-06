package com.sep.aiservice.controller;

import com.sep.aiservice.dto.IllustrationRequest;
import com.sep.aiservice.dto.IllustrationResponse;
import com.sep.aiservice.entity.Illustration;
import com.sep.aiservice.enums.IsActived;
import com.sep.aiservice.repository.IllustrationRepository;
import com.sep.aiservice.service.IllustrationService;
import com.sep.aiservice.service.impl.IllustrationServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/illustrations")
@RequiredArgsConstructor
@Validated
public class IllustrationController {

    private final IllustrationService illustrationService;

    @GetMapping
    public List<IllustrationResponse> getIllustrations() {
        return illustrationService.getAll();
    }

    @GetMapping("/{id}")
    public IllustrationResponse getIllustration(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id) {
        return illustrationService.getById(id);
    }

    // GET
    @GetMapping("/title/{title}")
    public IllustrationResponse getByTitle(
            @PathVariable @Size(max = 50) String title) {
        return illustrationService.getByTitle(title);
    }

    // CREATE
    @PostMapping
    public List<IllustrationResponse> createIllustrations(
            @RequestBody @Valid List<IllustrationRequest> requests) {
        return illustrationService.create(requests);
    }

    // UPDATE
    @PutMapping("/{id}")
    public IllustrationResponse updateIllustration(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid IllustrationRequest request) {
        return illustrationService.update(id, request);
    }

    // SOFT DELETE
    @DeleteMapping("/{id}")
    public void deleteIllustration(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        illustrationService.softDelete(id);
    }

    // SEARCH
    @GetMapping("/search")
    public Page<IllustrationResponse> search(
            @RequestParam(required = false) @Size(max = 50) String style,
            @RequestParam(required = false) @Size(max = 10) String format,
            @RequestParam(required = false) @Size(max = 50) String title,
            @RequestParam(required = false) IsActived isActived,
            @RequestParam(required = false) @Size(max = 50) String userId,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return illustrationService.search(style, format, title, isActived, userId, pageable);
    }
}