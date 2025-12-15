package com.saffrongardens.saffron.service;

import com.saffrongardens.saffron.entity.Booking;
import com.saffrongardens.saffron.entity.PaymentRecord;

import java.util.Map;

public interface PaymentService {
    /**
     * Create a payment intent / reference for the given booking. Implementation may return gateway-specific fields.
     */
    Map<String, Object> createPaymentIntent(Booking booking);

    /**
     * Handle an incoming webhook payload from the gateway. Returns the persisted PaymentRecord or null.
     */
    PaymentRecord handleWebhook(String rawBody, Map<String, String> headers) throws Exception;
}
