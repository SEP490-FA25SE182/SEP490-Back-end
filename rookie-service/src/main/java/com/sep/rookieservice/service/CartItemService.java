package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CartItemRequest;
import com.sep.rookieservice.dto.CartItemResponse;

import java.util.List;

public interface CartItemService {
    List<CartItemResponse> getAll();
    CartItemResponse getById(String id);
    List<CartItemResponse> getByCartId(String cartId);
    CartItemResponse create(String cartId, CartItemRequest request);
    CartItemResponse update(String id, CartItemRequest request);
    void delete(String id);
    void clearCart(String cartId);
}
