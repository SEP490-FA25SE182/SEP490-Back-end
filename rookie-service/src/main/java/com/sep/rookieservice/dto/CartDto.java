package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private String cartId;
    private int amount;
    private double totalPrice;
    private String userId;
    private Instant updatedAt;
    private IsActived isActived;
}

