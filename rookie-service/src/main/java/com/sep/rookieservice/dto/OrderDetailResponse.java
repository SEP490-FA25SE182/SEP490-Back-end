package com.sep.rookieservice.dto;

import lombok.Data;

@Data
public class OrderDetailResponse {
    private String orderDetailId;
    private int quantity;
    private double price;
    private String orderId;
    private String bookId;
}
