package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserRequest {
    @Size(max = 50)
    private String fullName;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @Pattern(regexp = "^(?i)(male|female|other)$", message = "Gender must be Male/Female/Other")
    private String gender;

    @Email
    @Size(max = 254)
    private String email;

    @Size(min = 8, max = 100, message = "Password length must be 8-100")
    private String password;

    @Size(max = 10)
    @Pattern(regexp = "^[0-9+\\-()\\s]*$", message = "Invalid phone format")
    private String phoneNumber;

    @Size(max = 1000)
    private String avatarUrl;

    @Size(max = 50)
    private String roleId;

    private IsActived isActived;

    private double royalty = 0.0;
}
