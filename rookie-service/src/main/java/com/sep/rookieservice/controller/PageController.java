package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.PageRequestDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService service;

    @PostMapping
    public PageResponseDTO create(@Valid @RequestBody PageRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public PageResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public PageResponseDTO update(@PathVariable String id, @Valid @RequestBody PageRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.softDelete(id);
    }

    // ✅ Pagination + Search + Sort
    @GetMapping
    public Page<PageResponseDTO> list(
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
        return service.search(q, chapterId, isActived, pageable);
    }
}
