package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.AuthDtos;
import com.sep.rookieservice.dto.AuthResponse;
import com.sep.rookieservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rookie/users/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        AuthResponse res = authService.loginWithGoogle(idToken);
        return ResponseEntity.ok(res);
    }

    /* LOCAL REGISTER */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthDtos.RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    /* LOCAL LOGIN */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthDtos.LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    /* CHANGE PASSWORD (yêu cầu currentPassword) */
    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestBody AuthDtos.ChangePasswordRequest req) {
        authService.changePassword(req);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

    /* FORGOT PASSWORD: phát hành token */
    @PostMapping("/password/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody AuthDtos.ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok(Map.of("message", "Đã gửi email đặt lại mật khẩu (nếu email tồn tại)"));
    }

    /* RESET PASSWORD bằng token */
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody AuthDtos.ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
    }

    /* LOGOUT */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        long ttl = authService.logout(authHeader);
        return ResponseEntity.ok(Map.of("message", "Đã logout", "token_invalid_in_seconds", ttl));
    }
}

