package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserResponse {
    private String userId;
    private String fullName;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String roleId;
    private Instant updatedAt;
    private IsActived isActived;
    private double royalty = 0.0;
}

