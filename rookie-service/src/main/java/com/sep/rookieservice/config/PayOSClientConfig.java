package com.sep.rookieservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
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

    @Bean(name = "payOSPayoutWebClient")
    WebClient payOSPayoutWebClient(PayOSProperties props) {
        if (isBlank(props.getPayoutBaseUrl()) ||
                isBlank(props.getPayoutClientId()) ||
                isBlank(props.getPayoutApiKey())) {
            throw new IllegalStateException(
                    "Missing payout credentials. Please set rookie.payos.payout-* properties");
        }

        WebClient.Builder b = WebClient.builder()
                .baseUrl(trim(props.getPayoutBaseUrl()))
                .defaultHeader("x-client-id", trim(props.getPayoutClientId()))
                .defaultHeader("x-api-key", trim(props.getPayoutApiKey()))
                .filter(logHeaders("PAYOUT"));

        if (!isBlank(props.getPayoutPartnerCode())) {
            b.defaultHeader("x-partner-code", trim(props.getPayoutPartnerCode()));
        }
        return b.build();
    }

    // --- helpers ---
    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private static String trim(String s) { return s == null ? null : s.trim(); }

    private static ExchangeFilterFunction logHeaders(String tag) {
        return (request, next) -> {
            String cid = request.headers().getFirst("x-client-id");
            String ak  = request.headers().getFirst("x-api-key");
            String sig = request.headers().getFirst("x-signature");
            String idem= request.headers().getFirst("x-idempotency-key");
            String partner = request.headers().getFirst("x-partner-code");
            System.out.println("[PayOS-"+tag+"] " + request.method() + " " + request.url());
            System.out.println("[PayOS-"+tag+"] x-client-id=" + mask(cid) +
                    " x-api-key=" + mask(ak) +
                    " x-signature=" + mask(sig) +
                    " x-idempotency-key=" + (idem == null ? "null" : idem) +
                    " x-partner-code=" + (partner == null ? "null" : partner));
            return next.exchange(request);
        };
    }
    private static String mask(String s) {
        if (s == null) return "null";
        if (s.length() <= 6) return "****";
        return s.substring(0, 3) + "****" + s.substring(s.length() - 3);
    }
}
