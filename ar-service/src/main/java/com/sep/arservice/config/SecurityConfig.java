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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/actuator/**"),
                                new AntPathRequestMatcher("/api/**")
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/actuator/**"),
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .formLogin(f -> f.disable())
                .httpBasic(b -> {});
        return http.build();
    }
}
