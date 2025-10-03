package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BookshelveRequestDTO;
import com.sep.rookieservice.dto.BookshelveResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.BookshelveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public Page<BookshelveResponseDTO> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) IsActived isActived,
            Pageable pageable
    ) {
        return service.search(q, userId, isActived, pageable);
    }
}