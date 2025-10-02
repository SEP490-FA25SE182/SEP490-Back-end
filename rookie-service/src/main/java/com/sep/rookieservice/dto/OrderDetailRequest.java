package com.sep.rookieservice.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderDetailRequest {

    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;

    @Min(value = 0, message = "price must be >= 0")
    private Double price;

}
