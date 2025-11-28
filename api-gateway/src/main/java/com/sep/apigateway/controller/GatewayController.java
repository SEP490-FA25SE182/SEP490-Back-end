package com.sep.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class GatewayController {

    @GetMapping("/")
    public Mono<Map<String, String>> home() {
        return Mono.just(Map.of(
            "service", "API Gateway",
            "version", "1.0.0",
            "description", "API Gateway for ARBook microservices",
            "swagger-ui", "/swagger-ui.html"
        ));
    }

    @GetMapping("/health")
    public Mono<Map<String, String>> health() {
        return Mono.just(Map.of(
            "status", "UP",
            "service", "API Gateway"
        ));
    }
}