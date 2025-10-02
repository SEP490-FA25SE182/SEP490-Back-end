package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;

@Data
public class RoleResponse {
    private String roleId;
    private String roleName;
    private IsActived isActived;
    private Instant createdAt;
}
