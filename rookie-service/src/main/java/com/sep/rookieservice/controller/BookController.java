package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BookRequestDTO;
import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/books")
public class BookController {

    private final BookService svc;

    @Autowired
    public BookController(BookService svc) {
        this.svc = svc;
    }

    @PostMapping
    public BookResponseDTO create(@Valid @RequestBody BookRequestDTO dto) {
        return svc.create(dto);
    }

    @GetMapping("/{id}")
    public BookResponseDTO getById(@PathVariable String id) {
        return svc.getById(id);
    }

    @PutMapping("/{id}")
    public BookResponseDTO update(@PathVariable String id, @Valid @RequestBody BookRequestDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.softDelete(id);
    }

    /**
     * Search & pagination endpoint.
     * - page: 0-based page index
     * - size: page size
     * - sort: e.g. sort=bookName,asc or multiple: sort=bookName,asc&sort=createdAt,desc
     * - q: search query for name + description
     * - authorId, publicationStatus, progressStatus
     * - isActived: ACTIVE or INACTIVE (enum name)
     */
    @GetMapping
    public Page<BookResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) Byte publicationStatus,
            @RequestParam(required = false) Byte progressStatus,
            @RequestParam(required = false) IsActived isActived
    ) {

        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            // Each sort entry is like "field,asc" or "field,desc"
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
        return svc.search(q, authorId, publicationStatus, progressStatus, isActived, pageable);
    }
}
