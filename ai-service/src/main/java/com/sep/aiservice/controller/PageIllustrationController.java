package com.sep.aiservice.controller;

import com.sep.aiservice.dto.PageIllustrationRequest;
import com.sep.aiservice.dto.PageIllustrationResponse;
import com.sep.aiservice.service.PageIllustrationService;
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
@RequestMapping("/api/rookie/page-illustrations")
@RequiredArgsConstructor
@Validated
public class PageIllustrationController {

    private final PageIllustrationService service;

    @GetMapping
    public List<PageIllustrationResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public PageIllustrationResponse getOne(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @PostMapping
    public List<PageIllustrationResponse> create(@RequestBody @Valid List<PageIllustrationRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public PageIllustrationResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid PageIllustrationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteHard(@PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        service.deleteHard(id);
    }

    @GetMapping("/search")
    public Page<PageIllustrationResponse> search(
            @RequestParam(required = false) @Size(max = 50) String pageId,
            @RequestParam(required = false) @Size(max = 50) String illustrationId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(pageId, illustrationId, pageable);
    }
}

