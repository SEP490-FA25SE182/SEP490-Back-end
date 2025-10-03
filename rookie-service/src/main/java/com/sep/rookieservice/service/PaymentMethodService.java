package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.PaymentMethodRequest;
import com.sep.rookieservice.dto.PaymentMethodResponse;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentMethodService {
    PaymentMethodResponse create(PaymentMethodRequest req);
    PaymentMethodResponse getById(String id);
    PaymentMethodResponse update(String id, PaymentMethodRequest req);
    void softDelete(String id);
    Page<PaymentMethodResponse> search(String q, IsActived isActived, Pageable pageable);
}


