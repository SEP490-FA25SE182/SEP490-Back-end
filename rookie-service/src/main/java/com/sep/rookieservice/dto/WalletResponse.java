package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class WalletResponse {
    private String walletId;
    private int coin;
    private double balance;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
