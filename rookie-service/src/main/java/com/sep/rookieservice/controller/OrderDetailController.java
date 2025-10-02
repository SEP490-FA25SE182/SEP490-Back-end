package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.OrderDetailRequest;
import com.sep.rookieservice.dto.OrderDetailResponse;
import com.sep.rookieservice.entity.OrderDetail;
import com.sep.rookieservice.service.OrderDetailService;
import com.sep.rookieservice.service.impl.OrderDetailServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/order/order-details")
@RequiredArgsConstructor
@Validated
public class OrderDetailController {

    private final OrderDetailService service;

    @GetMapping
    public List<OrderDetailResponse> getOrderDetails() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OrderDetailResponse getOrderDetail(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        return service.getById(id);
    }

    @GetMapping("/order/{orderId}")
    public List<OrderDetailResponse> getByOrder(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String orderId) {
        return service.getByOrderId(orderId);
    }

    @PostMapping
    public List<OrderDetailResponse> createOrderDetails(@RequestBody @Valid List<OrderDetailRequest> requests) {
        return service.create(requests);
    }

    @PutMapping("/{id}")
    public OrderDetailResponse updateOrderDetail(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id,
            @RequestBody @Valid OrderDetailRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteOrderDetail(
            @PathVariable @Pattern(regexp = "^[0-9a-fA-F\\-]{36}$") String id) {
        service.delete(id);
    }
}