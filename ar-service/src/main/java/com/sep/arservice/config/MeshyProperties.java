package com.sep.arservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "meshy")
@Data
public class MeshyProperties {
    private String baseUrl = "https://api.meshy.ai";
    private String apiKey;
    private Duration pollInterval = Duration.ofSeconds(3);
    private Duration timeout = Duration.ofMinutes(5);
}
