package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BookshelveRequestDTO;
import com.sep.rookieservice.dto.BookshelveResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.BookshelveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/bookshelves")
@RequiredArgsConstructor
public class BookshelveController {

    private final BookshelveService service;

    @PostMapping
    public BookshelveResponseDTO create(@Valid @RequestBody BookshelveRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public BookshelveResponseDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public BookshelveResponseDTO update(@PathVariable String id, @Valid @RequestBody BookshelveRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.softDelete(id);
    }

    @GetMapping
    public Page<BookshelveResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
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
        return service.search(q, userId, isActived, pageable);
    }

    @GetMapping("/{userId}/bookshelves")
    public Page<BookshelveResponseDTO> getBookshelvesByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split("-");
                String prop = parts[0].trim();
                Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return service.getBookshelvesByUserId(userId, pageable);
    }

}