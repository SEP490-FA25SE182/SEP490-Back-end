package com.sep.rookieservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDtos {

    @Data
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotBlank
        @Size(max = 50)
        private String fullName;

        @NotBlank
        @Email
        @Size(max = 254)
        private String email;

        @NotBlank
        @Size(min = 8, max = 100, message = "Password length must be 8-100")
        private String password;

        @Size(max = 20)
        @Pattern(regexp = "^[0-9+\\-()\\s]*$", message = "Invalid phone format")
        private String phoneNumber;  // optional

        private String roleId;
    }

    @Data
    @NoArgsConstructor
    public static class LoginRequest {
        @NotBlank
        @Email
        @Size(max = 254)
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        @NotBlank
        @Email
        @Size(max = 254)
        private String email;

        @NotBlank
        private String currentPassword;

        @NotBlank
        @Size(min = 8, max = 100, message = "Password length must be 8-100")
        private String newPassword;
    }

    @Data
    @NoArgsConstructor
    public static class ForgotPasswordRequest {
        @NotBlank
        @Email
        @Size(max = 254)
        private String email;
    }

    @Data
    @NoArgsConstructor
    public static class ResetPasswordRequest {
        @NotBlank
        private String token;

        @NotBlank
        @Size(min = 8, max = 100, message = "Password length must be 8-100")
        private String newPassword;
    }
}
