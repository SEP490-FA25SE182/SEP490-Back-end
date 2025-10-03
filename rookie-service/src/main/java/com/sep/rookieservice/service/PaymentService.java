package com.sep.rookieservice.service;

import com.sep.rookieservice.dto.CreateCheckoutResponse;
import com.sep.rookieservice.dto.WebhookPayload;

public interface PaymentService {
    CreateCheckoutResponse createCheckout(String orderId);
    void handleWebhook(WebhookPayload payload);
}

