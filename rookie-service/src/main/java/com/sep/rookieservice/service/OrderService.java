package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.BookResponseDTO;
import com.sep.rookieservice.dto.OrderRequest;
import com.sep.rookieservice.dto.OrderResponse;
import com.sep.rookieservice.enums.OrderEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    List<OrderResponse> getAll();
    OrderResponse getById(String id);
    List<OrderResponse> getByCartId(String cartId);
    List<OrderResponse> getByWalletId(String walletId);
    List<OrderResponse> create(List<OrderRequest> requests);
    OrderResponse update(String id, OrderRequest request);
    void delete(String id);
    OrderResponse moveCartToOrder(String cartId, String walletId, boolean usePoints, List<String> cartItemIds);
    Page<OrderResponse> search(String userId, OrderEnum status, Pageable pageable);
    Page<BookResponseDTO> getPurchasedBooks(
            String userId,
            String q,
            OrderEnum status,
            String genreId,
            String bookshelfId,
            Pageable pageable
    );
}
