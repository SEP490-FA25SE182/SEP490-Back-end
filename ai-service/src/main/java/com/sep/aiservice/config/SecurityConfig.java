package com.sep.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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
    private String frontendAltUrl;

    @Value("${PROD_FRONTEND_URL:https://frontend.arbookrookie.xyz}")
    private String prodFrontendUrl;

    @Value("${PROD_BACKEND_URL:https://backend.arbookrookie.xyz}")
    private String prodBackendUrl;

    @Value("${API_URL:http://localhost}")
    private String apiUrl;

    @Value("${GATEWAY_PORT:8080}")
    private String gatewayPort;

    @Value("${AI_PORT:8082}")
    private String aiPort;

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
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**",
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
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                frontendUrl,
                frontendAltUrl,
                prodFrontendUrl,
                prodBackendUrl,
                apiUrl + ":" + gatewayPort,
                apiUrl + ":" + aiPort
        ));

        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","Accept","X-Requested-With","Origin","X-User-Id"));
        config.setExposedHeaders(List.of("Location","Content-Disposition"));
        config.setAllowCredentials(true);
        config.setMaxAge(Duration.ofHours(1));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
