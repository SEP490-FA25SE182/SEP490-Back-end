package com.sep.rookieservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rookie.payos")
@Getter
@Setter
public class PayOSProperties {
    private String baseUrl;
    private String clientId;
    private String apiKey;
    private String checksumKey; // dùng tạo/verify signature
    private String returnUrl;
    private String cancelUrl;
}

