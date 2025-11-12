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

    @Bean
    public OpenAPI openAPI(
            @Value("${API_URL:http://localhost}") String apiUrl,
            @Value("${GATEWAY_PORT:8080}") String gatewayPort,
            @Value("${ROOKIE_PORT:8081}") String rookiePort,
            @Value("${server.servlet.context-path:}") String contextPath
    ) {
        String ctx = (contextPath == null || contextPath.isBlank()) ? "" :
                (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        String viaGateway = String.format("%s:%s/api/rookie", apiUrl, gatewayPort);
        String direct = String.format("%s:%s%s", apiUrl, rookiePort, ctx);

        return new OpenAPI()
                .info(new Info()
                        .title("Rookie Service API")
                        .version("1.0.0")
                        .description("API documentation for Rookie microservice")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url(viaGateway).description("Through API Gateway"),
                        new Server().url(direct).description("Direct service")
                ));
    }
}
