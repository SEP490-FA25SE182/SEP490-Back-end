package com.sep.rookieservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDtos {
    @Data
    @NoArgsConstructor
    public static class RegisterRequest {
        private String fullName;
        private String email;
        private String password;     // plain text FE gửi lên
        private String phoneNumber;  // optional
    }

    @Data
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        private String email;
        private String currentPassword;
        private String newPassword;
    }

    @Data
    @NoArgsConstructor
    public static class ForgotPasswordRequest {
        private String email;
    }

    @Data
    @NoArgsConstructor
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
    }

}
