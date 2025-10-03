package com.sep.rookieservice.gateway;

import com.sep.rookieservice.dto.PayOSCreateLinkRequest;
import com.sep.rookieservice.dto.PayOSCreateLinkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PayOSClient {
    private final WebClient payOSWebClient;

    public PayOSCreateLinkResponse createPaymentLink(PayOSCreateLinkRequest req) {
        return payOSWebClient.post()
                .uri("/v2/payment-requests")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(PayOSCreateLinkResponse.class)
                .block();
    }

    public Map<String,Object> getPaymentRequest(Object id) {
        return payOSWebClient.get()
                .uri("/v2/payment-requests/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();
    }

    public void cancelPaymentRequest(Object id, String reason) {
        Map<String,String> body = Map.of("cancellationReason", reason);
        payOSWebClient.post()
                .uri("/v2/payment-requests/{id}/cancel", id)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}

