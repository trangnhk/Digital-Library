/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.trangnhk.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/payments/stripe")
public class StripeWebhookController {
    @Autowired
    private PaymentService paymentService;
    
    @Value("${stripe.webhook_secret}")
    private String stripeWebhookSecret;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader) {
        String payload;

        try {
            payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        } catch (IOException ex) {
            ex.printStackTrace();

            return ResponseEntity.badRequest().body(Map.of("message", "Cannot read Stripe webhook payload: " + ex.getMessage()));
        }

        Event event;

        try {
            String secret = this.stripeWebhookSecret == null ? null : this.stripeWebhookSecret.trim();

            event = Webhook.constructEvent(payload, sigHeader, secret);

        } catch (SignatureVerificationException ex) {
            ex.printStackTrace();

            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Stripe signature: " + ex.getMessage()));

        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity.badRequest().body(Map.of("message", "Invalid Stripe webhook payload: " + ex.getMessage()));
        }

        String eventType = event.getType();

        System.out.println("Received Stripe event: " + eventType);

        if (!"checkout.session.completed".equals(eventType)) {
            return ResponseEntity.ok(Map.of(
                    "received", true,
                    "ignored", eventType
            ));
        }

        try {
            JsonNode root = this.objectMapper.readTree(payload);

            JsonNode objectNode = root
                    .path("data")
                    .path("object");

            String stripeSessionId = objectNode
                    .path("id")
                    .asText(null);

            String paymentIntentId = objectNode
                    .path("payment_intent")
                    .asText(null);

            if (stripeSessionId == null || stripeSessionId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Stripe session id is missing"
                ));
            }

            System.out.println("Checkout session id = " + stripeSessionId);
            System.out.println("Payment intent id = " + paymentIntentId);

            this.paymentService.handleStripeCheckoutCompleted(
                    stripeSessionId,
                    paymentIntentId
            );

            return ResponseEntity.ok(Map.of(
                    "received", true,
                    "processed", eventType
            ));

        } catch (Exception ex) {
            ex.printStackTrace();

            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Cannot process checkout.session.completed: " + ex.getMessage()
            ));
        }
    }
    
}
