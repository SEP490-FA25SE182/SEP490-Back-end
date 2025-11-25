package com.sep.rookieservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rookie.payos")
@Getter
@Setter
public class PayOSProperties {
    // ====== Payment (thu tiền) ======
    private String baseUrl;
    private String clientId;
    private String apiKey;
    private String checksumKey; // dùng tạo/verify signature
    private String returnUrl;
    private String cancelUrl;

    // ====== Payout (chi hộ) ======
    private String payoutBaseUrl;
    private String payoutClientId;
    private String payoutApiKey;
    private String payoutChecksumKey;
    private String payoutPartnerCode;
    private String createPayoutPath;

    private String payoutSignMode = "CANON5_RAW";

    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPass;
}

