package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class CartItemResponse {
    private String cartItemId;
    private int quantity;
    private double price;
    private String cartId;
    private String bookId;
}
