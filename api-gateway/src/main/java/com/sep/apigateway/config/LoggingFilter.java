package com.sep.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUri = exchange.getRequest().getURI();
        String path = requestUri.getPath();
        String method = exchange.getRequest().getMethod().toString();
        
        logger.info("=== Gateway Request ===");
        logger.info("Method: {}", method);
        logger.info("Path: {}", path);
        logger.info("Full URI: {}", requestUri);
        logger.info("Headers: {}", exchange.getRequest().getHeaders());
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("=== Gateway Response ===");
            logger.info("Status Code: {}", exchange.getResponse().getStatusCode());
            logger.info("Response Headers: {}", exchange.getResponse().getHeaders());
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
