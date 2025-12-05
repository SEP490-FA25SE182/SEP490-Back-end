package com.sep.rookieservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsGlobalConfig {

    @Value("${API_URL:http://localhost}")
    private String apiUrl;

    @Value("${GATEWAY_PORT:8080}")
    private String gatewayPort;

    @Value("${FRONTEND_URL:http://localhost:5173")
    private String frontendUrl;

    @Value("${PROD_FRONTEND_URL:https://www.arbookrookie.xyz")
    private String frontendUrlProduct;

    @Value("${FRONTEND_URL_DEPLOY:https://sep490-front-16600s0n8-quang-tn-beyonds-projects.vercel.app/")
    private String frontendUrlProduct2;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        String gatewayUrl = apiUrl + ":" + gatewayPort;

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://localhost:*",
                frontendUrl,
                "https://arbookrookie.xyz",
                "https://www.arbookrookie.xyz",
                "https://sep490-front-16600s0n8-quang-tn-beyonds-projects.vercel.app/",
                "https://sep490-front-end-quang-tn-beyonds-projects.vercel.app",
                "https://*.vercel.app"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
