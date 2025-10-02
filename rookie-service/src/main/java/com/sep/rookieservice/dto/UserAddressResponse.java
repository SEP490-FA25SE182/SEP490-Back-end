package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class UserAddressResponse {
    private String userAddressId;
    private String addressInfor;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
