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

    @Value("${API_URL:http://localhost}")
    private String apiUrl;

    @Value("${PROD_BACKEND_URL:https://backend.arbookrookie.xyz}")
    private String prodBackendUrl;

    @Value("${GATEWAY_PORT:8080}")
    private int gatewayPort;

    @Value("${AI_PORT:8082}")
    private int aiPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${AI_BASE_PATH:/api/ai}")
    private String aiBasePath;

    @Bean
    public OpenAPI openAPI() {
        String ctx = (contextPath == null || contextPath.isBlank())
                ? ""
                : (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        // =======================
        // LOCAL ENVIRONMENT
        // =======================
        String localDirect   = apiUrl + ":" + aiPort + ctx;
        String localGateway  = apiUrl + ":" + gatewayPort + aiBasePath;

        // =======================
        // PRODUCTION ENVIRONMENT
        // =======================
        String prodDirect    = prodBackendUrl + ctx;
        String prodGateway   = prodBackendUrl + aiBasePath;

        return new OpenAPI()
                .info(new Info()
                        .title("AI Service API")
                        .version("1.0.0")
                        .description("API documentation for AI microservice - Image Generation & Processing")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                )
                .servers(List.of(
                        new Server()
                                .url(localGateway)
                                .description("Local - Through API Gateway"),
                        new Server()
                                .url(localDirect)
                                .description("Local - Direct AI Service"),
                        new Server()
                                .url(prodGateway)
                                .description("Production - Through API Gateway"),
                        new Server()
                                .url(prodDirect)
                                .description("Production - Direct AI Service")
                ));
    }
}