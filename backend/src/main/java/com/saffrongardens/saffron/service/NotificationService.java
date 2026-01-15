package com.saffrongardens.saffron.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private final String frontendBase;
    private final String fromAddress;

    public NotificationService(@Value("${app.frontend.base-url:http://localhost:3000}") String frontendBase,
                               @Value("${saffron.email.from:no-reply@saffrongardens.com}") String fromAddress) {
        this.frontendBase = frontendBase;
        this.fromAddress = fromAddress;
    }

    @Async
    public void sendCanCompleteNotification(String email, String role) {
        if (mailSender == null) return; // mail not configured in this environment
        if (email == null || email.isBlank()) return;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setFrom(fromAddress);
        msg.setSubject("Your Saffron application is ready to be completed");
        String text = "Hi,\n\n" +
                "An admin has approved your request to join Saffron as a " + role + ". You can now complete your application here:\n" +
                frontendBase + "/complete-profile\n\n" +
                "If you didn't request access, ignore this email.\n\n" +
                "Regards,\nSaffron Gardens Team";
        msg.setText(text);
        mailSender.send(msg);
    }
}
