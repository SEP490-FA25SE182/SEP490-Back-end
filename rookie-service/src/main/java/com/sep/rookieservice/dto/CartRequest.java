package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class CartRequest {
    @Min(value = 0, message = "amount must be >= 0")
    private Integer amount;        // sẽ bị IGNORE trong update

    @Min(value = 0, message = "totalPrice must be >= 0")
    private Double totalPrice;     // sẽ bị IGNORE trong update

    private IsActived isActived;
}

