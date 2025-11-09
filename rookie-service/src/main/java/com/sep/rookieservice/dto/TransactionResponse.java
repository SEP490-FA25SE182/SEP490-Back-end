package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.enums.TransactionType;
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
    private String walletId;
    private IsActived isActived;
    private TransactionType transType;
    private Instant createdAt;
    private Instant updatedAt;
}
