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
    private String payoutBaseUrl;      // ví dụ: https://api-merchant.payos.vn
    private String payoutClientId;     // KHÁC clientId thu tiền
    private String payoutApiKey;       // KHÁC apiKey thu tiền
    private String payoutChecksumKey;
    private String payoutPartnerCode;  // nếu PayOS yêu cầu
    private String createPayoutPath;
}

