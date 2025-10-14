package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.NotificationRequestDTO;
import com.sep.rookieservice.dto.NotificationResponseDTO;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rookie/notifications")
public class NotificationController {

    private final NotificationService svc;

    @Autowired
    public NotificationController(NotificationService svc) {
        this.svc = svc;
    }

    @PostMapping
    public NotificationResponseDTO create(@Valid @RequestBody NotificationRequestDTO dto) {
        return svc.create(dto);
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO getById(@PathVariable String id) {
        return svc.getById(id);
    }

    @PutMapping("/{id}")
    public NotificationResponseDTO update(@PathVariable String id, @Valid @RequestBody NotificationRequestDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.softDelete(id);
    }

    @GetMapping
    public Page<NotificationResponseDTO> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String bookId,
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) IsActived isActived
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null) {
            for (String s : sort) {
                if (s == null || s.trim().isEmpty()) continue;
                String[] parts = s.split(",");
                Sort.Direction dir = Sort.Direction.ASC;
                if (parts.length > 1) {
                    try { dir = Sort.Direction.fromString(parts[1].trim()); } catch (IllegalArgumentException ignored) {}
                }
                sortObj = sortObj.and(Sort.by(dir, parts[0].trim()));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return svc.search(q, userId, bookId, orderId, isActived, pageable);
    }
}
