package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.OrderDetailDto;
import com.sep.rookieservice.model.OrderDetail;
import com.sep.rookieservice.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rookie/users/orders/order-details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @GetMapping
    public List<OrderDetail> getOrderDetails() {
        return orderDetailService.getAllOrderDetails();
    }

    @GetMapping("/{id}")
    public OrderDetail getOrderDetail(@PathVariable String id) {
        return orderDetailService.findById(id).get();
    }

    @GetMapping("/order/{orderId}")
    public List<OrderDetail> getDetailsByOrder(@PathVariable String orderId) {
        return orderDetailService.findByOrderId(orderId);
    }

    @PostMapping
    public List<OrderDetail> createOrderDetails(@RequestBody List<OrderDetail> details) {
        return orderDetailService.createOrderDetails(details);
    }

    @PutMapping("/{id}")
    public OrderDetail updateOrderDetail(@PathVariable String id, @RequestBody OrderDetailDto dto) {
        return orderDetailService.updateOrderDetail(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteOrderDetail(@PathVariable String id) {
        orderDetailService.deleteOrderDetail(id);
    }
}

