package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.GenreRequestDTO;
import com.sep.rookieservice.dto.GenreResponseDTO;
import com.sep.rookieservice.service.GenreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/genres")
public class GenreController {

    private final GenreService svc;

    @Autowired
    public GenreController(GenreService svc) {
        this.svc = svc;
    }

    @PostMapping
    public GenreResponseDTO create(@Valid @RequestBody GenreRequestDTO dto) {
        return svc.create(dto);
    }

    @PutMapping("/{id}")
    public GenreResponseDTO update(@PathVariable String id, @Valid @RequestBody GenreRequestDTO dto) {
        return svc.update(id, dto);
    }

    @GetMapping("/{id}")
    public GenreResponseDTO getById(@PathVariable String id) {
        return svc.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.delete(id);
    }

    @GetMapping
    public Page<GenreResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String keyword
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split(",");
                String field = parts[0].trim();
                Sort.Direction dir = (parts.length > 1) ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC;
                sortObj = sortObj.and(Sort.by(dir, field));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return svc.search(keyword, pageable);
    }
}
