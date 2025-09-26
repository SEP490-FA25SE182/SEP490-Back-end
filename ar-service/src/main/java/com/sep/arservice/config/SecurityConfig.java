package com.sep.arservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Cho phép API không cần auth
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/**")
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                // H2 console cần frame từ same-origin
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                // Tắt CSRF cho H2 console & API (POST/PUT...)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                // Không cần form login
                .formLogin(f -> f.disable())
                .httpBasic(b -> {});
        return http.build();
    }
}
