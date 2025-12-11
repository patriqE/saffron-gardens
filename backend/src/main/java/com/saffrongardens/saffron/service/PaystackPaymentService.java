package com.saffrongardens.saffron.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saffrongardens.saffron.entity.Booking;
import com.saffrongardens.saffron.entity.PaymentRecord;
import com.saffrongardens.saffron.repository.PaymentRecordRepository;
import com.saffrongardens.saffron.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaystackPaymentService implements PaymentService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final BookingRepository bookingRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.paystack.secret:change_me}")
    private String paystackSecret;

    public PaystackPaymentService(PaymentRecordRepository paymentRecordRepository, BookingRepository bookingRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Map<String, Object> createPaymentIntent(Booking booking) {
        // Minimal stub: return a client-side reference that frontend will use with Paystack
        String reference = "booking_" + booking.getId() + "_" + System.currentTimeMillis();
        Map<String, Object> m = new HashMap<>();
        m.put("reference", reference);
        m.put("amount", booking.getTotalAmount());
        m.put("currency", "NGN");
        // In a real integration you'd call Paystack API to initialize transaction and return authorization
        return m;
    }

    @Override
    public PaymentRecord handleWebhook(String rawBody, Map<String, String> headers) throws Exception {
        // Verify HMAC-SHA512 signature from Paystack before processing
        String sigHeader = null;
        if (headers != null) sigHeader = headers.get("x-paystack-signature");
        if (sigHeader == null || sigHeader.isBlank()) {
            // missing signature - reject
            return null;
        }

        // compute HMAC-SHA512 over the raw body
        byte[] secretBytes = paystackSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(secretBytes, "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(keySpec);
        byte[] computed = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
        String computedHex = bytesToHex(computed);
        if (!computedHex.equalsIgnoreCase(sigHeader)) {
            // signature mismatch
            return null;
        }

        // Very small, forgiving parser: now that signature is verified, parse payload and persist
        JsonNode root = mapper.readTree(rawBody);
        JsonNode event = root.path("event");
        JsonNode data = root.path("data");
        if (data.isMissingNode()) return null;

        // look for metadata.bookingId
        JsonNode metadata = data.path("metadata");
        Long bookingId = null;
        if (metadata.has("bookingId")) {
            bookingId = metadata.get("bookingId").asLong();
        }

        BigDecimal amount = BigDecimal.ZERO;
        if (data.has("amount")) amount = new BigDecimal(data.get("amount").asLong()).divide(new BigDecimal(100));

        String status = data.path("status").asText(data.path("gateway_response").asText(""));
        String gateway = "PAYSTACK";
        String tx = data.path("reference").asText();

        PaymentRecord pr = new PaymentRecord();
        if (bookingId != null) {
            com.saffrongardens.saffron.entity.Booking b = bookingRepository.findById(bookingId).orElse(null);
            if (b != null) pr.setBooking(b);
        }
        pr.setAmount(amount);
        pr.setCurrency("NGN");
        pr.setGateway(gateway);
        pr.setGatewayTransaction(tx);
        pr.setStatus(status);

        return paymentRecordRepository.save(pr);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public boolean handleWebhook(String rawBody, String signature) {
        try {
            java.util.Map<String, String> headers = new java.util.HashMap<>();
            if (signature != null) headers.put("x-paystack-signature", signature);
            PaymentRecord r = handleWebhook(rawBody, headers);
            return r != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
