package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.AnswerRequestDTO;
import com.sep.rookieservice.dto.AnswerResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService service;

    @PostMapping
    public AnswerResponseDTO create(@RequestBody AnswerRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public AnswerResponseDTO update(@PathVariable String id, @RequestBody AnswerRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public AnswerResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    /**
     * Search & pagination endpoint for answers.
     * Supports keyword, questionId, isCorrect, and isActived filters.
     */
    @GetMapping
    public Page<AnswerResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String questionId,
            @RequestParam(required = false) Boolean isCorrect,
            @RequestParam(required = false) IsActived isActived
    ) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split(",");
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
        return service.search(q, questionId, isCorrect, isActived, pageable);
    }
}
