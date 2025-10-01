package com.sep.rookieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private String cartItemId;
    private int quantity;
    private double price;
    private String cartId;
    private String bookId;
}
