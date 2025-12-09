package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.ChapterRequestDTO;
import com.sep.rookieservice.dto.ChapterResponseDTO;
import com.sep.rookieservice.dto.PageResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/books/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService service;

    @PostMapping
    public ChapterResponseDTO create(@RequestBody ChapterRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ChapterResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ChapterResponseDTO update(@PathVariable String id, @RequestBody ChapterRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void softDelete(@PathVariable String id) {
        service.softDelete(id);
    }

    @GetMapping
    public Page<ChapterResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String bookId,
            @RequestParam(required = false) Byte progressStatus,
            @RequestParam(required = false) IsActived isActived
    ) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            // Each sort entry is like "field,asc" or "field,desc"
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
        return service.search(q, bookId, progressStatus, isActived, pageable);
    }

    @GetMapping("/{chapterId}/pages")
    public List<PageResponseDTO> getPagesByChapterId(@PathVariable String chapterId) {
        return service.getPagesByChapterId(chapterId);
    }
}
