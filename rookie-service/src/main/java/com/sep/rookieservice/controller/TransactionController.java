package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.TransactionRequest;
import com.sep.rookieservice.dto.TransactionResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.TransactionEnum;
import com.sep.rookieservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    public TransactionResponse create(@RequestBody @Valid TransactionRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable String id, @RequestBody @Valid TransactionRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.softDelete(id);
    }

    @GetMapping("/search")
    public Page<TransactionResponse> search(
            @RequestParam(required = false) TransactionEnum status,
            @RequestParam(required = false) IsActived isActived,
            @RequestParam(required = false) String paymentMethodName,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(status, isActived, paymentMethodName, pageable);
    }
}
