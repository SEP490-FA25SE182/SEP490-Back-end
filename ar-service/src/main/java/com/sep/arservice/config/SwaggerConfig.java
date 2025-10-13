package com.sep.arservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(
            @Value("${server.port:8083}") int port,
            @Value("${server.servlet.context-path:}") String contextPath
    ) {
        String ctx = (contextPath == null || contextPath.isBlank()) ? "" :
                (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        String direct = "http://localhost:" + port + ctx;              // http://localhost:8083
        String viaGateway = "http://localhost:8080/api/ar";        // route qua Gateway

        return new OpenAPI()
                .info(new Info()
                        .title("AR Service API")
                        .version("1.0.0")
                        .description("API documentation for Rookie microservice")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url(viaGateway).description("Through API Gateway"),
                        new Server().url(direct).description("Direct service")
                ));
    }
}
