package com.sep.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsGlobalConfig {

    @Value("${FRONTEND_URL:8080}")
    private String frontendUrl;

    @Value("${FRONTEND_URL_ALT:8080}")
    private String frontendUrlAlt;

    @Value("${API_URL:http://localhost}")
    private String apiUrl;

    @Value("${GATEWAY_PORT:8080}")
    private String gatewayPort;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        String gatewayUrl = apiUrl + ":" + gatewayPort;

        config.setAllowedOrigins(List.of(
                gatewayUrl,
                frontendUrlAlt,
                frontendUrl
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
