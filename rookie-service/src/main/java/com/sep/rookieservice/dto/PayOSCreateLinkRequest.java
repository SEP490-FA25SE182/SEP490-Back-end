package com.sep.rookieservice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PayOSCreateLinkRequest {
    private long orderCode;
    private int amount;
    private String description;
    private String cancelUrl;
    private String returnUrl;
    private String signature; // HMAC từ 5 field trên
}
