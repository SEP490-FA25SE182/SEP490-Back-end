package com.sep.rookieservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

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

    /*@Bean(name = "payOSPayoutWebClient")
    WebClient payOSPayoutWebClient(PayOSProperties props) {
        String proxyHost = props.getProxyHost();
        int    proxyPort = props.getProxyPort();
        String proxyUser = props.getProxyUser();
        String proxyPass = props.getProxyPass();

        HttpClient http = HttpClient.create()
                .proxy(p -> p
                                .type(ProxyProvider.Proxy.HTTP)
                                .host(proxyHost)
                                .port(proxyPort)
                                .username(proxyUser)
                                .password(ignored -> proxyPass)
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(http))
                .baseUrl(props.getPayoutBaseUrl().trim())
                .defaultHeader("x-client-id", props.getPayoutClientId().trim())
                .defaultHeader("x-api-key",   props.getPayoutApiKey().trim())
                .filter(logHeaders("PAYOUT"))
                .build();
    }

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
    }*/
}
