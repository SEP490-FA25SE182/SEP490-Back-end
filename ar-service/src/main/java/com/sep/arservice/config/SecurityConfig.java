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
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/api/**"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/actuator/**")
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html","/swagger-ui/**",
                                "/v3/api-docs","/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .formLogin(f -> f.disable())
                .httpBasic(b -> {});
        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOrigins(java.util.List.of(
                "http://localhost:8082",
                "http://127.0.0.1:8082",
                "http://192.168.1.37:8082",
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));

        // Nếu muốn “cho hết” trong dev: dùng patterns thay vì setAllowedOrigins
        // config.setAllowedOriginPatterns(java.util.List.of("*"));

        config.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(java.util.List.of(
                "Authorization","Content-Type","Accept","X-Requested-With","Origin","X-User-Id"
        ));
        config.setExposedHeaders(java.util.List.of("Location","Content-Disposition"));
        config.setAllowCredentials(true);
        config.setMaxAge(java.time.Duration.ofHours(1));

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}