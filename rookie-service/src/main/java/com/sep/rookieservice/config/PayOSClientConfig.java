package com.sep.rookieservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(PayOSProperties.class)
public class PayOSClientConfig {
    @Bean
    WebClient payOSWebClient(PayOSProperties props) {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader("x-client-id", props.getClientId())
                .defaultHeader("x-api-key", props.getApiKey())
                .build();
    }
}
