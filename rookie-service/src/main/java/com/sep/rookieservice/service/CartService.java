package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CartRequest;
import com.sep.rookieservice.dto.CartResponse;

import java.util.List;

public interface CartService {
    List<CartResponse> getAll();
    CartResponse getById(String id);
    CartResponse getByUserId(String userId);
    List<CartResponse> create(List<CartRequest> requests);
    CartResponse update(String id, CartRequest request);
    void softDelete(String id);
}
