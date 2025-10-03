package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.PaymentMethodRequest;
import com.sep.rookieservice.dto.PaymentMethodResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.service.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/payment-methods")
@RequiredArgsConstructor
@Validated
public class PaymentMethodController {

    private final PaymentMethodService service;

    @PostMapping
    public PaymentMethodResponse create(@Valid @RequestBody PaymentMethodRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public PaymentMethodResponse getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public PaymentMethodResponse update(@PathVariable String id,
                                        @Valid @RequestBody PaymentMethodRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.softDelete(id);
    }

    @GetMapping("/search")
    public Page<PaymentMethodResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) IsActived isActived,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return service.search(q, isActived, pageable);
    }
}

