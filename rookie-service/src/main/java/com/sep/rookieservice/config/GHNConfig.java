package com.sep.rookieservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GHNConfig {

    @Value("${ghn.base-url}")
    private String baseUrl;

    @Bean
    public WebClient ghnWebClient(WebClient.Builder builder) {
        return builder.baseUrl(baseUrl).build();
    }
}
