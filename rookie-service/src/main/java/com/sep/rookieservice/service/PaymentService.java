package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CreateCheckoutResponse;
import com.sep.rookieservice.dto.WebhookPayload;

public interface PaymentService {
    CreateCheckoutResponse createCheckout(String orderId, String returnUrl, String cancelUrl);
    void handleWebhook(WebhookPayload payload);
}

