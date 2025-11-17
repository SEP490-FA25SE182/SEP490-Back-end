package com.sep.rookieservice.config;

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

    @Value("${API_URL}")
    private String apiUrl;

    @Value("${PROD_BACKEND_URL}")
    private String prodBackendUrl;

    @Value("${server.port:8081}")
    private int port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${ROOKIE_BASE_PATH:/api/rookie}")
    private String rookieBasePath;

    @Bean
    public OpenAPI openAPI() {

        String ctx = (contextPath == null || contextPath.isBlank()) ? "" :
                (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        // Build full URLs
        String localDirect = apiUrl + ":" + port + ctx;
        String localGateway = apiUrl + ":8080" + rookieBasePath;

        String prodDirect = prodBackendUrl + ":" + port + ctx;
        String prodGateway = prodBackendUrl + rookieBasePath;

        return new OpenAPI()
                .info(new Info()
                        .title("Rookie Service API")
                        .version("1.0.0")
                        .description("API documentation for Rookie microservice")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url(localGateway).description("Localhost - Through API Gateway"),
                        new Server().url(localDirect).description("Localhost - Direct Service"),
                        new Server().url(prodGateway).description("Production - Through API Gateway"),
                        new Server().url(prodDirect).description("Production - Direct Service")
                ));
    }
}
