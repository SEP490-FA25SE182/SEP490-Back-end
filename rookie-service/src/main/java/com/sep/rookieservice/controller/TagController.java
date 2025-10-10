package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.TagRequest;
import com.sep.rookieservice.dto.TagResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.TagService;
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
@RequestMapping("/api/rookie/tags")
@RequiredArgsConstructor
@Validated
public class TagController {

    private final TagService service;

    @GetMapping
    public List<TagResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public TagResponse getOne(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @PostMapping
    public List<TagResponse> create(@RequestBody @Valid List<TagRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public TagResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid TagRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        service.softDelete(id);
    }

    @GetMapping("/search")
    public Page<TagResponse> search(
            @RequestParam(required = false) @Size(max = 100) String name,
            @RequestParam(required = false) IsActived isActived,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(name, isActived, pageable);
    }
}
