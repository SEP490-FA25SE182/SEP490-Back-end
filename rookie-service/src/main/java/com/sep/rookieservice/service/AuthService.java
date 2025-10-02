package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.AuthDtos;
import com.sep.rookieservice.dto.AuthResponse;

public interface AuthService {
    AuthResponse loginWithGoogle(String idToken);
    AuthResponse register(AuthDtos.RegisterRequest req);
    AuthResponse login(AuthDtos.LoginRequest req);
    void changePassword(AuthDtos.ChangePasswordRequest req);
    void forgotPassword(AuthDtos.ForgotPasswordRequest req);
    void resetPassword(AuthDtos.ResetPasswordRequest req);
    long logout(String bearerToken);
}
