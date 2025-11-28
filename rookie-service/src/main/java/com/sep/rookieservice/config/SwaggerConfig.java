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

    @Value("${API_URL:http://localhost}")
    private String apiUrl;

    @Value("${PROD_BACKEND_URL:https://backend.arbookrookie.xyz}")
    private String prodBackendUrl;

    @Value("${GATEWAY_PORT:8080}")
    private int gatewayPort;

    @Value("${ROOKIE_PORT:8081}")
    private int rookiePort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${ROOKIE_BASE_PATH:/api/rookie}")
    private String rookieBasePath;

    @Bean
    public OpenAPI openAPI() {

        // Normalize context-path
        String ctx = (contextPath == null || contextPath.isBlank())
                ? ""
                : (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        // -----------------------
        // LOCAL ENVIRONMENT URLs
        // -----------------------
        String localDirect = apiUrl + ":" + rookiePort + ctx;
        String localGateway = apiUrl + ":" + gatewayPort + rookieBasePath;

        // -----------------------
        // PRODUCTION ENVIRONMENT
        // -----------------------
        String prodDirect = prodBackendUrl + ctx;
        String prodGateway = prodBackendUrl + rookieBasePath;

        return new OpenAPI()
                .info(new Info()
                        .title("Rookie Service API")
                        .version("1.0.0")
                        .description("API documentation for Rookie microservice")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .servers(List.of(
                        new Server().url(localGateway).description("Local - Through API Gateway"),
                        new Server().url(localDirect).description("Local - Direct Rookie Service"),
                        new Server().url(prodGateway).description("Production - Through API Gateway"),
                        new Server().url(prodDirect).description("Production - Direct Rookie Service")
                ));
    }
}
