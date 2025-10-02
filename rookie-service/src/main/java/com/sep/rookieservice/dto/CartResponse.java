package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class CartResponse {
    private String cartId;
    private int amount;
    private double totalPrice;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
