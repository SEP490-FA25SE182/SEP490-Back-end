package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.ContractRequestDTO;
import com.sep.rookieservice.dto.ContractResponseDTO;
import com.sep.rookieservice.dto.UpdateContractStatusRequest;
import com.sep.rookieservice.enums.ContractStatus;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService service;

    @PostMapping
    public ResponseEntity<ContractResponseDTO> create(@RequestBody ContractRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractResponseDTO> update(@PathVariable String id, @RequestBody ContractRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContractResponseDTO> updateStatus(
            @PathVariable String id,
            @RequestBody UpdateContractStatusRequest req
    ) {
        return ResponseEntity.ok(service.changeStatus(id, req.getStatus()));
    }

    @GetMapping
    public Page<ContractResponseDTO> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ContractStatus status,
            @RequestParam(required = false) IsActived isActived
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null) {
            for (String s : sort) {
                String[] parts = s.split("-");
                String field = parts[0];
                Sort.Direction dir = parts.length > 1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
                sortObj = sortObj.and(Sort.by(dir, field));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        return service.search(q, status, isActived, pageable);
    }
}
