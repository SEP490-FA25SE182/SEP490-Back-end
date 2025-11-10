package com.sep.aiservice.controller;

import com.sep.aiservice.dto.PageAudioRequest;
import com.sep.aiservice.dto.PageAudioResponse;
import com.sep.aiservice.service.PageAudioService;
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
@RequestMapping("/api/rookie/page-audios")
@RequiredArgsConstructor
@Validated
public class PageAudioController {

    private final PageAudioService service;

    @GetMapping
    public List<PageAudioResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public PageAudioResponse getOne(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @PostMapping
    public List<PageAudioResponse> create(@RequestBody @Valid List<PageAudioRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public PageAudioResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid PageAudioRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteHard(@PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        service.deleteHard(id);
    }

    @GetMapping("/search")
    public Page<PageAudioResponse> search(
            @RequestParam(required = false) @Size(max = 50) String pageId,
            @RequestParam(required = false) @Size(max = 50) String audioId,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(pageId, audioId, pageable);
    }
}
