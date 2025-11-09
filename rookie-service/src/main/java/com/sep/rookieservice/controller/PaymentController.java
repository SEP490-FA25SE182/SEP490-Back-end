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
    public ResponseEntity<CreateCheckoutResponse> createCheckout(
            @PathVariable String orderId,
            @RequestParam(required = false) String returnUrl,
            @RequestParam(required = false) String cancelUrl
    ) {
        return ResponseEntity.ok(paymentService.createCheckout(orderId, returnUrl, cancelUrl));
    }

    // Webhook: cần mở public, bỏ auth
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody WebhookPayload payload) {
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wallets/{walletId}/deposit")
    public ResponseEntity<CreateCheckoutResponse> deposit(
            @PathVariable String walletId,
            @RequestParam int amount,
            @RequestParam(required = false) String returnUrl,
            @RequestParam(required = false) String cancelUrl
    ) {
        return ResponseEntity.ok(paymentService.deposit(amount, walletId, returnUrl, cancelUrl));
    }

    @PostMapping("/wallets/{walletId}/withdraw")
    public ResponseEntity<CreateCheckoutResponse> withdraw(
            @PathVariable String walletId,
            @RequestParam int amount,
            @RequestParam String accountName,
            @RequestParam String bankName,
            @RequestParam String accountNumber,
            @RequestParam(required = false) String returnUrl,
            @RequestParam(required = false) String cancelUrl
    ) {
        return ResponseEntity.ok(
                paymentService.withdraw(
                        amount, walletId, accountName, bankName, accountNumber, returnUrl, cancelUrl
                )
        );
    }

}

