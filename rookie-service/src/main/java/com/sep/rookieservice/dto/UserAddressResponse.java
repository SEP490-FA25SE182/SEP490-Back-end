package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class UserAddressResponse {
    private String userAddressId;
    private String addressInfor;
    private String phoneNumber;
    private String fullName;
    private String type;
    private boolean isDefault;
    private String userId;
    private Instant createdAt;
    private Instant updatedAt;
    private IsActived isActived;
}
