package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.OrderDetailRequest;
import com.sep.rookieservice.dto.OrderDetailResponse;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetailResponse> getAll();
    OrderDetailResponse getById(String id);
    List<OrderDetailResponse> getByOrderId(String orderId);
    List<OrderDetailResponse> create(List<OrderDetailRequest> requests);
    OrderDetailResponse update(String id, OrderDetailRequest request);
    void delete(String id);
}
