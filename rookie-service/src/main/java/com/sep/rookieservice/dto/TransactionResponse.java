package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class TransactionResponse {
    private String transactionId;
    private double totalPrice;
    private Byte status;
    private String paymentMethodId;
    private String orderId;
    private Long orderCode;
    private IsActived isActived;
    private Instant createdAt;
    private Instant updatedAt;
}
