package com.sep.rookieservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayOSPayoutRequest {
    private Long orderCode;
    private Integer amount;
    private String description;
    private String cancelUrl;
    private String returnUrl;

    private String accountName;
    private String bankName;
    private String accountNumber;

    private String signature;
}

