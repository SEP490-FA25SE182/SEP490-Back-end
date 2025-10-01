package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.OrderDto;
import com.sep.rookieservice.model.Order;
import com.sep.rookieservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable String id) {
        return orderService.findById(id).get();
    }

    @PostMapping
    public List<Order> createOrders(@RequestBody List<Order> orders) {
        return orderService.createOrders(orders);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable String id, @RequestBody OrderDto dto) {
        return orderService.updateOrder(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
    }

    // Move cart to order
    @PostMapping("/from-cart/{cartId}/wallet/{walletId}")
    public Order moveCartToOrder(@PathVariable String cartId, @PathVariable String walletId) {
        return orderService.moveCartToOrder(cartId, walletId);
    }
}

