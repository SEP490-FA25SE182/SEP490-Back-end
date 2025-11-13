package com.sep.rookieservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(
            @Value("${server.port:8081}") int port,
            @Value("${server.servlet.context-path:}") String contextPath
    ) {
        String ctx = (contextPath == null || contextPath.isBlank()) ? "" :
                (contextPath.startsWith("/") ? contextPath : "/" + contextPath);

        String localhostGateway = "http://localhost:8080/api/rookie";
        String localhostDirect = "http://localhost:" + port + ctx;
        String domainGateway = "https://backend.arbookrookie.xyz/api/rookie";
        String domainDirect = "https://backend.arbookrookie.xyz:" + port + ctx;

        return new OpenAPI()
                .info(new Info()
                        .title("Rookie Service API")
                        .version("1.0.0")
                        .description("API documentation for Rookie microservice")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url(localhostGateway).description("Localhost - Through API Gateway"),
                        new Server().url(localhostDirect).description("Localhost - Direct Service"),
                        new Server().url(domainGateway).description("Domain - Through API Gateway"),
                        new Server().url(domainDirect).description("Domain - Direct Service")
                ));
    }
}
