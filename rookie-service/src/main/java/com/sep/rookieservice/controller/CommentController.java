package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.CommentRequestDTO;
import com.sep.rookieservice.dto.CommentResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping
    public CommentResponseDTO create(@RequestBody CommentRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CommentResponseDTO update(@PathVariable String id, @RequestBody CommentRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public CommentResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @GetMapping
    public Page<CommentResponseDTO> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String blogId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) IsActived isActived
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split(",");
                String prop = parts[0].trim();
                Sort.Direction dir = parts.length > 1 ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return service.search(q, blogId, userId, isActived, pageable);
    }
}
