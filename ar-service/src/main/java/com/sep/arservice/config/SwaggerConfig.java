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

        String localhostDirect = "http://localhost:" + port + ctx;
        String localhostGateway = "http://localhost:8080/api/ar";
        String productionDirect = "http://backend.arbookrookie.xyz:" + port + ctx;
        String productionGateway = "http://backend.arbookrookie.xyz/api/ar";

        return new OpenAPI()
                .info(new Info()
                        .title("AR Service API")
                        .version("1.0.0")
                        .description("API documentation for AR microservice")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url(localhostGateway).description("Localhost - Through API Gateway"),
                        new Server().url(localhostDirect).description("Localhost - Direct Service"),
                        new Server().url(productionGateway).description("Production - Through API Gateway"),
                        new Server().url(productionDirect).description("Production - Direct Service")
                ));
    }
}
