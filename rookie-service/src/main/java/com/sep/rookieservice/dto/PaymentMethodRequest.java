package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PaymentMethodRequest {
    @Size(max = 50)
    private String methodName;
    @Size(max = 50)
    private String provider;
    @Size(max = 250)
    private String decription;
    private IsActived isActived;
}

