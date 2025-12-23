package com.sep.rookieservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderRequest {
    @Min(value = 0, message = "amount must be >= 0")
    private Integer amount;

    @Min(value = 0, message = "totalPrice must be >= 0")
    private Double totalPrice;

    @NotNull(message = "status is required")
    private Byte status;

    private String cartId;

    private String walletId;

    private String userAddressId;

    private String reason;

    private String imageUrl;

    private Double shippingFee;
}
