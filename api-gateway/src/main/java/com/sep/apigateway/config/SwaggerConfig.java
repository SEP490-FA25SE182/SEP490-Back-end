package com.sep.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI-Powered AR Book API Gateway")
                        .version("1.0.0")
                        .description("API documentation for AR Book API Gateway")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Localhost - API Gateway"),
                        new Server().url("https://backend.arbookrookie.xyz").description("Production - API Gateway")
                ));
    }
}
