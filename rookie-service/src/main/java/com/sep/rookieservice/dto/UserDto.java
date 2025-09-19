package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;

import java.time.LocalDate;

public class UserDto {
    private String userId;
    private String fullName;
    private LocalDate birthDate;
    private String gender;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String role;
    private IsActived isActived;
}
