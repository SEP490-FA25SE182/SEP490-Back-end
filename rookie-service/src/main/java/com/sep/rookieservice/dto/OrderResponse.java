package com.sep.rookieservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class OrderResponse {
    private String orderId;
    private int amount;
    private double totalPrice;
    private Byte status;
    private String walletId;
    private String cartId;
    private Instant createdAt;
    private Instant updatedAt;
}
