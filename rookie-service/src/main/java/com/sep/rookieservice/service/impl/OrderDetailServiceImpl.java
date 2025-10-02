package com.sep.rookieservice.service.impl;

import com.sep.rookieservice.dto.OrderDetailRequest;
import com.sep.rookieservice.dto.OrderDetailResponse;
import com.sep.rookieservice.entity.OrderDetail;
import com.sep.rookieservice.mapper.OrderDetailMapper;
import com.sep.rookieservice.repository.OrderDetailRepository;
import com.sep.rookieservice.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderDetailMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allOrderDetails", key = "'all'")
    public List<OrderDetailResponse> getAll() {
        return orderDetailRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "OrderDetail", key = "#id")
    public OrderDetailResponse getById(String id) {
        var d = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found: " + id));
        return mapper.toResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetailResponse> getByOrderId(String orderId) {
        return orderDetailRepository.findByOrderId(orderId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allOrderDetails", "OrderDetail"}, allEntries = true)
    public List<OrderDetailResponse> create(List<OrderDetailRequest> requests) {
        var entities = requests.stream().map(req -> {
            var d = new OrderDetail();
            mapper.copyForCreate(req, d);
            return d;
        }).toList();
        return orderDetailRepository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Override
    @CacheEvict(value = {"allOrderDetails", "OrderDetail"}, allEntries = true)
    public OrderDetailResponse update(String id, OrderDetailRequest request) {
        var d = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found: " + id));
        mapper.copyForUpdate(request, d);
        return mapper.toResponse(orderDetailRepository.save(d));
    }

    @Override
    @CacheEvict(value = {"allOrderDetails", "OrderDetail"}, allEntries = true)
    public void delete(String id) {
        var d = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found: " + id));
        orderDetailRepository.delete(d);
    }
}

