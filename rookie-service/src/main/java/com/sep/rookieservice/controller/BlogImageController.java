package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BlogImageRequest;
import com.sep.rookieservice.dto.BlogImageResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.BlogImageService;
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
@RequestMapping("/api/rookie/blogs/blog-images")
@RequiredArgsConstructor
@Validated
public class BlogImageController {

    private final BlogImageService service;

    @GetMapping
    public List<BlogImageResponse> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public BlogImageResponse getOne(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @PostMapping
    public List<BlogImageResponse> create(@RequestBody @Valid List<BlogImageRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public BlogImageResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid BlogImageRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        service.softDelete(id);
    }

    @GetMapping("/search")
    public Page<BlogImageResponse> search(
            @RequestParam(required = false) @Size(max = 50) String blogId,
            @RequestParam(required = false) @Size(max = 255) String altText,
            @RequestParam(required = false) IsActived isActived,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(blogId, altText, isActived, pageable);
    }
}
