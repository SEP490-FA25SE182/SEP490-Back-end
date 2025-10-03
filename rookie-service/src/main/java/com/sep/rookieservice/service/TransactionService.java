package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.TransactionRequest;
import com.sep.rookieservice.dto.TransactionResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.TransactionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionResponse create(TransactionRequest req);
    TransactionResponse getById(String id);
    TransactionResponse update(String id, TransactionRequest req);
    void softDelete(String id);
    Page<TransactionResponse> search(TransactionEnum status, IsActived isActived, String paymentMethodName, Pageable pageable);
}
