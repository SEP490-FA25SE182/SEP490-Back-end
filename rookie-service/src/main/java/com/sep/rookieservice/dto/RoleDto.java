package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private String roleId;
    private String roleName;
    private IsActived isActived;
}
