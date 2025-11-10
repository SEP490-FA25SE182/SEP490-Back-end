package com.sep.aiservice.config;

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
            @Value("${server.port:8082}") int port,
            @Value("${server.servlet.context-path:}") String contextPath
    ) {
        String ctx = (contextPath == null || contextPath.isBlank()) ? "" :
                (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        // Localhost servers
        String localhostDirect = "http://localhost:" + port + ctx;              // http://localhost:8082
        String localhostGateway = "http://localhost:8080/api/ai";               // route qua Gateway

        // Production servers  
        String productionDirect = "https://backend.arbookrookie.xyz:" + port + ctx;    // Direct production
        String productionGateway = "https://backend.arbookrookie.xyz/api/ai";          // Production via Gateway

        return new OpenAPI()
                .info(new Info()
                        .title("AI Service API")
                        .version("1.0.0")
                        .description("API documentation for AI microservice")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url(localhostGateway).description("Localhost - Through API Gateway"),
                        new Server().url(localhostDirect).description("Localhost - Direct Service"),
                        new Server().url(productionGateway).description("Production - Through API Gateway"),
                        new Server().url(productionDirect).description("Production - Direct Service")
                ));
    }
}
