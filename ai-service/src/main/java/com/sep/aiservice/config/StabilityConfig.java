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
import java.util.Optional;

@ConditionalOnProperty(prefix="stability", name="enabled", havingValue="true")
@Configuration
public class StabilityConfig {

    @Bean("stabilityWebClient")
    public WebClient stabilityWebClient(
            @Value("${stability.base-url}") String baseUrl,
            @Value("${stability.api-key}") String rawKey,
            @Value("${stability.timeout-ms:60000}") long timeoutMs
    ) {
        final String apiKey = Optional.ofNullable(rawKey).map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new IllegalStateException("stability.api-key is empty or missing"));

        HttpClient httpClient = HttpClient.create()
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutMs)
                .responseTimeout(Duration.ofMillis(timeoutMs))
                .doOnConnected(c -> c
                        .addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS))
                        .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(baseUrl)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(256 * 1024 * 1024)) // 256MB
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}



