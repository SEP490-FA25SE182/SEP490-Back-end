package com.sep.rookieservice.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.sep.rookieservice.dto.AuthDtos;
import com.sep.rookieservice.dto.AuthResponse;
import com.sep.rookieservice.dto.UserDto;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.model.PasswordResetToken;
import com.sep.rookieservice.model.User;
import com.sep.rookieservice.repository.PasswordResetTokenRepository;
import com.sep.rookieservice.repository.UserRepository;
import com.sep.rookieservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final JwtBlacklistService blacklistService;

    private static final String DEFAULT_ROLE_ID = "6c97c75f-0e58-4dee-986c-19f5e065c4ea";

    @Transactional
    public AuthResponse loginWithGoogle(String idToken) {
        FirebaseToken decoded;
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("ID token không hợp lệ", e);
        }

        String email = decoded.getEmail();
        if (email == null || email.isBlank()) {
            // Có trường hợp Google account không share email
            throw new IllegalStateException("Không lấy được email từ tài khoản Google.");
        }

        // Optional: kiểm tra email đã verify chưa
        Boolean emailVerified = decoded.isEmailVerified();
        if (emailVerified == null || !emailVerified) {
            // tuỳ policy: có thể vẫn cho login, nhưng khuyến nghị yêu cầu verify, để bàn thêm
            // throw new IllegalStateException("Email Google chưa được xác minh.");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            String randomPlain = generateRandomPassword(32); // 32 ký tự
            String hashed = passwordEncoder.encode(randomPlain);

            user = new User();
            user.setFullName(decoded.getName() != null ? decoded.getName() : email);
            user.setEmail(email);
            user.setAvatarUrl(decoded.getPicture());
            user.setPassword(hashed);
            user.setRoleId(DEFAULT_ROLE_ID);
            user.setIsActived(IsActived.ACTIVE);

            // birthDate, gender, phoneNumber... nếu cần sẽ bổ sung sau
            userRepository.save(user);

            // randomPlain không trả về cho FE (tránh lộ). Người dùng nếu muốn đăng nhập bằng Email/Password
        } else {
            if (user.getIsActived() != null && user.getIsActived() != IsActived.ACTIVE) {
                throw new IllegalStateException("Tài khoản đang bị khoá/không hoạt động.");
            }

            // Optional: cập nhật avatar/fullName nếu muốn đồng bộ mỗi lần login
            if (decoded.getName() != null && !decoded.getName().equals(user.getFullName())) {
                user.setFullName(decoded.getName());
            }
            if (decoded.getPicture() != null && !decoded.getPicture().equals(user.getAvatarUrl())) {
                user.setAvatarUrl(decoded.getPicture());
            }
        }

        // Sinh JWT (subject có thể là userId hoặc email; ở đây dùng email)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roleId", user.getRoleId());
        String jwt = jwtProvider.generateToken(user.getEmail(), claims);

        // Map thủ công sang UserDto (giữ password trong DTO, nhưng KHÔNG trả về password cho FE)
        UserDto dto = toDtoWithoutPassword(user);

        return new AuthResponse(dto, jwt);
    }

    private UserDto toDtoWithoutPassword(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRoleId(user.getRoleId());
        dto.setUpdateAt(user.getUpdateAt());
        dto.setIsActived(user.getIsActived());
        return dto;
    }

    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /* ===== REGISTER (Email/Password) ===== */
    @Transactional
    public AuthResponse register(AuthDtos.RegisterRequest req) {
        userRepository.findByEmail(req.getEmail())
                .ifPresent(u -> { throw new IllegalStateException("Email đã tồn tại"); });

        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRoleId(DEFAULT_ROLE_ID);
        user.setIsActived(IsActived.ACTIVE);

        userRepository.save(user);

        String jwt = issueJwt(user);
        return new AuthResponse(toDto(user), jwt);
    }

    /* ===== LOGIN (Email/Password) ===== */
    @Transactional(readOnly = true)
    public AuthResponse login(AuthDtos.LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));

        if (user.getIsActived() != IsActived.ACTIVE) {
            throw new IllegalStateException("Tài khoản không hoạt động");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }
        String jwt = issueJwt(user);
        return new AuthResponse(toDto(user), jwt);
    }

    /* ===== CHANGE PASSWORD (có currentPassword) ===== */
    @Transactional
    public void changePassword(AuthDtos.ChangePasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    /* ===== FORGOT PASSWORD (phát hành token) ===== */
    @Transactional
    public void forgotPassword(AuthDtos.ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getUserId());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plusSeconds(15 * 60)); // 15 phút
        tokenRepository.save(token);

        // Gửi email thật (không trả token về response ở môi trường production)
        mailService.sendResetPasswordEmail(user.getEmail(), token.getToken());
    }

    /* ===== RESET PASSWORD (bằng token) ===== */
    @Transactional
    public void resetPassword(AuthDtos.ResetPasswordRequest req) {
        PasswordResetToken t = tokenRepository.findByToken(req.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (t.isUsed() || t.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Token đã dùng hoặc hết hạn");
        }

        User user = userRepository.findById(t.getUserId())
                .orElseThrow(() -> new IllegalStateException("Tài khoản không tồn tại"));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        t.setUsed(true);
        tokenRepository.save(t);
    }

    /* ===== Helpers ===== */
    private String issueJwt(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roleId", user.getRoleId());
        return jwtProvider.generateToken(user.getEmail(), claims);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(user.getGender());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRoleId(user.getRoleId());
        dto.setUpdateAt(user.getUpdateAt());
        dto.setIsActived(user.getIsActived());
        return dto;
    }

    /**
     * Trả về TTL còn lại của token (giây) và đưa jti vào blacklist.
     */
    public long logout(String bearerToken) {
        String token = extractToken(bearerToken);
        String jti = jwtProvider.getJti(token);
        Date exp = jwtProvider.getExpiration(token);
        long ttlSeconds = Math.max(1, (exp.getTime() - System.currentTimeMillis()) / 1000);
        blacklistService.blacklist(jti, ttlSeconds);
        return ttlSeconds;
    }

    private String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Thiếu hoặc sai định dạng Authorization header");
        }
        return bearerToken.substring(7);
    }
}
