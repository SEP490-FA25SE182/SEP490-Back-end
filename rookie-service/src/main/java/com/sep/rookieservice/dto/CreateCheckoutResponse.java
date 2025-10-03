package com.sep.rookieservice.dto;

import lombok.Builder;

@Builder
public record CreateCheckoutResponse(
        String checkoutUrl,
        String qrCode,
        String paymentLinkId,
        Long orderCode,
        int amount
) {}
