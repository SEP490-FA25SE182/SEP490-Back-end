package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
public class WalletRequest {
    @Min(value = 0, message = "coin must be >= 0")
    private Integer coin;

    private String userId;

    private IsActived isActived;
}
