package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.OrderDetailDto;
import com.sep.rookieservice.model.OrderDetail;
import com.sep.rookieservice.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    @Cacheable(value = "allOrderDetails", key = "'all'")
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Cacheable(value = "OrderDetail", key = "'id'")
    public Optional<OrderDetail> findById(String id) {
        return orderDetailRepository.findById(id);
    }

    public List<OrderDetail> findByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @CacheEvict(value = {"allOrderDetails", "OrderDetail"}, allEntries = true)
    public List<OrderDetail> createOrderDetails(List<OrderDetail> details) {
        return orderDetailRepository.saveAll(details);
    }

    @CacheEvict(value = {"allOrderDetails", "OrderDetail"}, allEntries = true)
    public OrderDetail updateOrderDetail(String id, OrderDetailDto dto) {
        OrderDetail detail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found with id: " + id));

        detail.setQuantity(dto.getQuantity());
        detail.setPrice(dto.getPrice());

        return orderDetailRepository.save(detail);
    }

    @CacheEvict(value = {"allOrderDetails", "OrderDetail"}, allEntries = true)
    public void deleteOrderDetail(String id) {
        OrderDetail detail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found with id: " + id));
        orderDetailRepository.delete(detail);
    }
}

