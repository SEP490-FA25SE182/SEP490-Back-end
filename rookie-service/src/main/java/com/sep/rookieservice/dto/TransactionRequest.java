package com.sep.rookieservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequest {
    @Min(value = 0, message = "totalPrice must be >= 0")
    private Double totalPrice;

    @NotNull(message = "status is required")
    private Byte status;

    private Long orderCode;

    private String orderId;

    private String paymentMethodId;

    private com.sep.rookieservice.enums.IsActived isActived;
}
