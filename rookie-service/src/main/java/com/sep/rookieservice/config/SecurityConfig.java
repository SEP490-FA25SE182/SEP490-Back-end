package com.sep.rookieservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Value("${FRONTEND_URL_ALT:http://127.0.0.1:5173}")
    private String frontendUrlAlt;

    @Value("${API_URL:http://localhost}")
    private String apiUrl;

    @Value("${GATEWAY_PORT:8080}")
    private String gatewayPort;

    @Value("${PROD_FRONTEND_URL:https://frontend.arbookrookie.xyz}")
    private String prodFrontend;

    @Value("${PROD_BACKEND_URL:https://backend.arbookrookie.xyz}")
    private String prodBackend;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
                                "/actuator/**",
                                "/api/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                frontendUrl,
                frontendUrlAlt,
                apiUrl + ":" + gatewayPort,
                prodFrontend,
                prodBackend
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        config.setExposedHeaders(List.of("Location", "Content-Disposition"));
        config.setAllowCredentials(true);
        config.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
