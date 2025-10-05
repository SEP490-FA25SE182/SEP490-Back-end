package com.sep.rookieservice.controller;

import com.sep.rookieservice.dto.CreateCheckoutResponse;
import com.sep.rookieservice.dto.WebhookPayload;
import com.sep.rookieservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rookie/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<CreateCheckoutResponse> createCheckout(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.createCheckout(orderId));
    }

    // Webhook: cần mở public, bỏ auth
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody WebhookPayload payload) {
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }
}

