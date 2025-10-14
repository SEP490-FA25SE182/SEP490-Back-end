package com.sep.aiservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@ConditionalOnProperty(value = "stability.enabled", havingValue = "true")
@Configuration
public class StabilityConfig {

    @Bean("stabilityWebClient")
    public WebClient stabilityWebClient(
            @Value("${stability.base-url}") String baseUrl,
            @Value("${stability.api-key}") String apiKey,
            @Value("${stability.timeout-ms:60000}") long timeoutMs
    ) {
        HttpClient httpClient = HttpClient.create()
                // timeout kết nối TCP
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutMs)
                // timeout đọc/ghi
                .responseTimeout(Duration.ofMillis(timeoutMs))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS))
                                .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS))
                );

        // tăng bộ đệm cho multipart/byte[]
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // 50MB
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }
}


