package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.OrderRequest;
import com.sep.rookieservice.dto.OrderResponse;
import com.sep.rookieservice.entity.Order;
import com.sep.rookieservice.enums.OrderEnum;
import com.sep.rookieservice.service.OrderService;
import com.sep.rookieservice.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getOrders() {
        return orderService.getAll();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$", message = "Invalid UUID format") String id) {
        return orderService.getById(id);
    }

    @GetMapping("/cart/{cartId}")
    public List<OrderResponse> getByCart(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String cartId) {
        return orderService.getByCartId(cartId);
    }

    @GetMapping("/wallet/{walletId}")
    public List<OrderResponse> getByWallet(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String walletId) {
        return orderService.getByWalletId(walletId);
    }

    @PostMapping
    public List<OrderResponse> createOrders(@RequestBody @Valid List<OrderRequest> requests) {
        return orderService.create(requests);
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrder(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid OrderRequest request) {
        return orderService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        orderService.delete(id);
    }

    // Move Cart -> Order
    @PostMapping("/from-cart/{cartId}/wallet/{walletId}")
    public OrderResponse moveCartToOrder(
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String cartId,
            @PathVariable
            @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String walletId,
            @RequestParam(name = "usePoints", defaultValue = "false") boolean usePoints) {

        return orderService.moveCartToOrder(cartId, walletId, usePoints);
    }

    // SEARCH
    @GetMapping("/search")
    public Page<OrderResponse> search(
            @RequestParam(required = false) OrderEnum status,
            @ParameterObject
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return orderService.search(status, pageable);
    }
}