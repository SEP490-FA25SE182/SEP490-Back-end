package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.FeedbackRequestDTO;
import com.sep.rookieservice.dto.FeedbackResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> create(@RequestBody FeedbackRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponseDTO> update(@PathVariable String id, @RequestBody FeedbackRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search & pagination endpoint for Feedbacks.
     * - page: 0-based page index
     * - size: page size
     * - sort: e.g. sort=createdAt,desc or multiple: sort=rating,desc&sort=createdAt,asc
     * - q: search keyword (content)
     * - bookId: filter by book
     * - userId: filter by user
     * - isActived: ACTIVE or INACTIVE (enum name)
     */
    @GetMapping
    public Page<FeedbackResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String bookId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) IsActived isActived
    ) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split("-");
                String prop = parts[0].trim();
                Sort.Direction dir = Sort.Direction.ASC;
                if (parts.length > 1) {
                    try {
                        dir = Sort.Direction.fromString(parts[1].trim());
                    } catch (IllegalArgumentException ignore) {}
                }
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return service.search(q, bookId, userId, isActived, pageable);
    }

}
