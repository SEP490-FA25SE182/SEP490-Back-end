package com.sep.rookieservice.security;

import com.sep.rookieservice.service.JwtBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final JwtBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtProvider.getClaims(token);
                String jti = claims.getId();
                if (blacklistService.isBlacklisted(jti)) {
                    // Token đã logout → từ chối
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Token has been logged out\"}");
                    return;
                }

                // … phần còn lại: set Authentication vào SecurityContext … chưa biết viết gì, nào nghĩ ra thì code
            } catch (Exception ex) {
                // token sai/hết hạn (như trên)
            }
        }

        chain.doFilter(request, response);
    }
}

