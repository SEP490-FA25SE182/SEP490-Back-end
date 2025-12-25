package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CreateCheckoutResponse;
import com.sep.rookieservice.dto.WebhookPayload;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.Map;

public interface PaymentService {
    CreateCheckoutResponse createCheckout(String orderId, String returnUrl, String cancelUrl);
    void handleWebhook(@RequestBody Map<String, Object> payload);
    CreateCheckoutResponse deposit(int amount, String walletId, String returnUrl, String cancelUrl);
    void handlePayOSRedirect(String status, String cancel, Long orderCode);
    CreateCheckoutResponse withdraw(
            int amount,
            String walletId,
            String accountName,
            String bankName,
            String accountNumber,
            String returnUrl,
            String cancelUrl
    );
}

