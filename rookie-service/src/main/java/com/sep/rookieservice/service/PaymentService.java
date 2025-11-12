package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CreateCheckoutResponse;
import com.sep.rookieservice.dto.WebhookPayload;

public interface PaymentService {
    CreateCheckoutResponse createCheckout(String orderId, String returnUrl, String cancelUrl);
    void handleWebhook(WebhookPayload payload);
    CreateCheckoutResponse deposit(int amount, String walletId, String returnUrl, String cancelUrl);
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

