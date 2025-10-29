package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.QuizRequestDTO;
import com.sep.rookieservice.dto.QuizResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/books/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService service;

    /**
     * Search & pagination endpoint.
     * - page: 0-based page index
     * - size: page size
     * - sort: e.g. sort=title,asc&sort=createdAt,desc
     * - q: search keyword
     * - chapterId: filter by chapter
     * - isActived: ACTIVE/INACTIVE
     */
    @GetMapping
    public Page<QuizResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String chapterId,
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
        return service.search(q, chapterId, isActived, pageable);
    }

    @PostMapping
    public QuizResponseDTO create(@RequestBody QuizRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public QuizResponseDTO update(@PathVariable String id, @RequestBody QuizRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public QuizResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
