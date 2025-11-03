package com.sep.rookieservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // main connection endpoint
                .setAllowedOrigins(
                        "http://localhost:5173", // your frontend (Vite)
                        "http://127.0.0.1:5173",
                        "https://frontend.arbookrookie.xyz" // production site
                )
                .withSockJS(); // fallback for browsers without native WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // prefix for sending messages
        registry.enableSimpleBroker("/topic"); // clients subscribe to /topic/*
    }
}
