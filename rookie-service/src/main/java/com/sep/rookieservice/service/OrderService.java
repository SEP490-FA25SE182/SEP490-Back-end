package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.OrderRequest;
import com.sep.rookieservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAll();
    OrderResponse getById(String id);
    List<OrderResponse> getByCartId(String cartId);
    List<OrderResponse> getByWalletId(String walletId);
    List<OrderResponse> create(List<OrderRequest> requests);
    OrderResponse update(String id, OrderRequest request);
    void delete(String id);
    OrderResponse moveCartToOrder(String cartId, String walletId);
}
