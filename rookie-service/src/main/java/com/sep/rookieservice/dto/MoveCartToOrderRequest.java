package com.sep.rookieservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class MoveCartToOrderRequest {
    private List<String> cartItemIds;
    // các field optional để thay cho updateOrder
    private Double totalPrice;
    private Byte status;
    private String userAddressId;
    private Double shippingFee;
}

