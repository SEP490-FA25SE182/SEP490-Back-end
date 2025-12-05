package com.sep.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI requestUri = exchange.getRequest().getURI();
        String path = requestUri.getPath();
        String method = exchange.getRequest().getMethod().toString();
        
        logger.info("╔════════════════════════════════════════");
        logger.info("║ GATEWAY REQUEST");
        logger.info("║ Method: {}", method);
        logger.info("║ Path: {}", path);
        logger.info("║ Query: {}", requestUri.getQuery());
        logger.info("║ Full URI: {}", requestUri);
        
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            logger.info("║ GATEWAY RESPONSE");
            logger.info("║ Matched Route: {}", route != null ? route.getId() : "NONE");
            logger.info("║ Route URI: {}", route != null ? route.getUri() : "NONE");
            logger.info("║ Status Code: {}", exchange.getResponse().getStatusCode());
            logger.info("╚════════════════════════════════════════");
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
