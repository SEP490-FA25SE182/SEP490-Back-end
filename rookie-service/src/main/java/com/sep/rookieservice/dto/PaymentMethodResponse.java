package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class PaymentMethodResponse {
    private String paymentMethodId;
    private String methodName;
    private String provider;
    private String decription;
    private IsActived isActived;
    private Instant createdAt;
}
