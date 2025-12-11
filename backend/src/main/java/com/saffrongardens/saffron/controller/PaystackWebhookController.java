package com.saffrongardens.saffron.controller;

import com.saffrongardens.saffron.service.PaystackPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments/paystack")
public class PaystackWebhookController {

    private final PaystackPaymentService paystackService;

    public PaystackWebhookController(PaystackPaymentService paystackService) {
        this.paystackService = paystackService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String payload, @RequestHeader(value = "x-paystack-signature", required = false) String signature) {
        // delegate to service; service is responsible for verification and persistence
        boolean ok = paystackService.handleWebhook(payload, signature);
        if (ok) return ResponseEntity.ok().build();
        return ResponseEntity.status(400).body("invalid webhook");
    }
}
