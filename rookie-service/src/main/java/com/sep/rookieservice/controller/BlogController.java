package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BlogRequest;
import com.sep.rookieservice.dto.BlogResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.UpdatedOrder;
import com.sep.rookieservice.service.BlogService;
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
import java.util.Set;

@RestController
@RequestMapping("/api/rookie/blogs")
@RequiredArgsConstructor
@Validated
public class BlogController {

    private final BlogService service;

    @GetMapping
    public List<BlogResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BlogResponse getOne(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format")
            String id
    ) {
        return service.getById(id);
    }

    @PostMapping
    public List<BlogResponse> create(@RequestBody @Valid List<BlogRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public BlogResponse update(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid BlogRequest request
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
    public Page<BlogResponse> search(
            @RequestParam(required = false) @Size(max = 200) String title,
            @RequestParam(required = false) @Size(max = 10000) String content,
            @RequestParam(required = false) @Size(max = 50) String authorId,
            @RequestParam(required = false) @Size(max = 50) String bookId,
            @RequestParam(required = false) IsActived isActived,
            @RequestParam(required = false) Set<@Size(max = 50) String> tagIds,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(title, content, authorId, bookId, isActived, tagIds, pageable);
    }

    @GetMapping("/filter")
    public Page<BlogResponse> filterByUpdated(
            @RequestParam(defaultValue = "LATEST") UpdatedOrder order,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.filterByUpdated(order, pageable);
    }

    @GetMapping("/search/user")
    public Page<BlogResponse> searchForUser(
            @RequestParam(required = false) @Size(max = 300) String q,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.searchForUser(q, pageable);
    }

}
