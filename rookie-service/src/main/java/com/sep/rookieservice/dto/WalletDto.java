package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {
    private String walletId;
    private int coin;
    private String userId;
    private Instant updatedAt;
    private IsActived isActived;
}
