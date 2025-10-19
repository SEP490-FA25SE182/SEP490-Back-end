package com.sep.rookieservice.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.sep.rookieservice.dto.AuthDtos;
import com.sep.rookieservice.dto.AuthResponse;
import com.sep.rookieservice.dto.UserResponse;
import com.sep.rookieservice.enums.IsActived;
import com.sep.rookieservice.entity.PasswordResetToken;
import com.sep.rookieservice.entity.User;
import com.sep.rookieservice.mapper.UserMapper;
import com.sep.rookieservice.repository.PasswordResetTokenRepository;
import com.sep.rookieservice.repository.RoleRepository;
import com.sep.rookieservice.repository.UserRepository;
import com.sep.rookieservice.security.JwtProvider;
import com.sep.rookieservice.service.AuthService;
import com.sep.rookieservice.service.JwtBlacklistService;
import com.sep.rookieservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final PasswordResetTokenRepository tokenRepository;
    private final MailService mailService;
    private final JwtBlacklistService blacklistService;
    private final UserMapper userMapper;

    private static final String DEFAULT_ROLE_NAME = "customer";
    private static final Set<String> ALLOWED_SIGNUP_ROLES = Set.of("customer", "author");

    @Override
    public AuthResponse loginWithGoogle(String idToken) {
        FirebaseToken decoded;
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("ID token không hợp lệ", e);
        }

        String email = normalizedEmail(decoded.getEmail());
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Không lấy được email từ tài khoản Google.");
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            String randomPlain = generateRandomPassword(32);
            String hashed = passwordEncoder.encode(randomPlain);

            user = new User();
            user.setFullName(decoded.getName() != null ? decoded.getName() : email);
            user.setEmail(email);
            user.setAvatarUrl(decoded.getPicture());
            user.setPassword(hashed);
            user.setRoleId(resolveActiveRoleIdByName(DEFAULT_ROLE_NAME));
            user.setIsActived(IsActived.ACTIVE);
            userRepository.save(user);
        } else {
            if (user.getIsActived() != null && user.getIsActived() != IsActived.ACTIVE) {
                throw new IllegalStateException("Tài khoản đang bị khoá/không hoạt động.");
            }
            if (decoded.getName() != null && !decoded.getName().equals(user.getFullName())) {
                user.setFullName(decoded.getName());
            }
            if (decoded.getPicture() != null && !decoded.getPicture().equals(user.getAvatarUrl())) {
                user.setAvatarUrl(decoded.getPicture());
            }
        }

        String jwt = issueJwt(user);
        UserResponse userRes = userMapper.toResponse(user);
        return new AuthResponse(userRes, jwt);
    }

    /* REGISTER */
    @Override
    public AuthResponse register(AuthDtos.RegisterRequest req) {
        String email = normalizedEmail(req.getEmail());
        userRepository.findByEmail(email)
                .ifPresent(u -> { throw new IllegalStateException("Email đã tồn tại"); });

        String resolvedRoleId = resolveSignupRoleIdByIdOrDefault(req.getRoleId());

        User user = new User();
        user.setFullName(req.getFullName().trim());
        user.setEmail(email);
        user.setPhoneNumber(req.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRoleId(resolvedRoleId);
        user.setIsActived(IsActived.ACTIVE);

        userRepository.save(user);

        String jwt = issueJwt(user);
        return new AuthResponse(userMapper.toResponse(user), jwt);
    }

    /* LOGIN */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthDtos.LoginRequest req) {
        String email = normalizedEmail(req.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));

        if (user.getIsActived() != IsActived.ACTIVE) {
            throw new IllegalStateException("Tài khoản không hoạt động");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }
        String jwt = issueJwt(user);
        roleRepository.findByRoleIdAndIsActived(user.getRoleId(), IsActived.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Role của tài khoản không ACTIVE"));
        return new AuthResponse(userMapper.toResponse(user), jwt);
    }

    /* CHANGE PASSWORD */
    @Override
    public void changePassword(AuthDtos.ChangePasswordRequest req) {
        String email = normalizedEmail(req.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        if (req.getNewPassword().equals(req.getCurrentPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }

    /* FORGOT PASSWORD */
    @Override
    public void forgotPassword(AuthDtos.ForgotPasswordRequest req) {
        String email = normalizedEmail(req.getEmail());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        PasswordResetToken token = new PasswordResetToken();
        token.setUserId(user.getUserId());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plusSeconds(15 * 60));
        tokenRepository.save(token);

        mailService.sendResetPasswordEmail(user.getEmail(), token.getToken());
    }

    /* RESET PASSWORD */
    @Override
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

    /* LOGOUT */
    @Override
    public long logout(String bearerToken) {
        String token = extractToken(bearerToken);
        String jti = jwtProvider.getJti(token);
        Date exp = jwtProvider.getExpiration(token);
        long ttlSeconds = Math.max(1, (exp.getTime() - System.currentTimeMillis()) / 1000);
        blacklistService.blacklist(jti, ttlSeconds);
        return ttlSeconds;
    }

    /* Helpers */
    private String issueJwt(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("roleId", user.getRoleId());
        return jwtProvider.generateToken(user.getEmail(), claims);
    }

    private String normalizedEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Thiếu hoặc sai định dạng Authorization header");
        }
        return bearerToken.substring(7);
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

    private String resolveActiveRoleIdByName(String roleName) {
        return roleRepository.findByRoleNameIgnoreCaseAndIsActived(roleName, IsActived.ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "Role '" + roleName + "' không tồn tại hoặc không ở trạng thái ACTIVE"))
                .getRoleId();
    }

    private String resolveSignupRoleIdByIdOrDefault(String requestedRoleId) {
        if (requestedRoleId == null || requestedRoleId.isBlank()) {
            return resolveActiveRoleIdByName(DEFAULT_ROLE_NAME);
        }
        var role = roleRepository.findByRoleIdAndIsActived(requestedRoleId, IsActived.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("roleId không tồn tại hoặc không ACTIVE."));
        ensureAllowedRoleName(role.getRoleName());
        return role.getRoleId();
    }

    private void ensureAllowedRoleName(String roleName) {
        if (roleName == null || !ALLOWED_SIGNUP_ROLES.contains(roleName.toLowerCase())) {
            throw new IllegalArgumentException("Chỉ được chọn role: Customer hoặc Author.");
        }
    }
}