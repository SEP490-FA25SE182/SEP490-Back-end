package com.sep.rookieservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration for Rookie Service.
 * IMPORTANT:
 *  - DO NOT set custom server URLs when using API Gateway.
 *  - Let SpringDoc auto-detect request base URL.
 *  - Gateway will rewrite path: /api/rookie/** -> rookie-service
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rookie Service API")
                        .description("API documentation for Rookie microservice")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}
