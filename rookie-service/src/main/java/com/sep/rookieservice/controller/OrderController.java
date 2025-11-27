package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.BookResponseDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            @RequestParam(name = "usePoints", defaultValue = "false") boolean usePoints,
            @RequestBody List<String> cartItemIds) {

        return orderService.moveCartToOrder(cartId, walletId, usePoints, cartItemIds);
    }

    // SEARCH
    @GetMapping("/search")
    public Page<OrderResponse> search(
            @RequestParam String userId,
            @RequestParam(required = false) OrderEnum status,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return orderService.search(userId, status, pageable);
    }

    @GetMapping("/purchased-books")
    public Page<BookResponseDTO> getPurchasedBooks(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) OrderEnum status,
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) String bookshelfId
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
                        dir = Sort.Direction.valueOf(parts[1].trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) {
                        if ("DESC".equalsIgnoreCase(parts[1].trim())) {
                            dir = Sort.Direction.DESC;
                        }
                    }
                }
                sortObj = sortObj.and(Sort.by(dir, prop));
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        return orderService.getPurchasedBooks(
                userId, q, status, genreId, bookshelfId, pageable
        );
    }
}