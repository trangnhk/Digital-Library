/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.CreatePaymentRequestDTO;
import com.trangnhk.dto.PaymentResponseDTO;

/**
 *
 * @author Admin
 */
public interface PaymentService {
    PaymentResponseDTO createPayment(String username, CreatePaymentRequestDTO dto);
    boolean checkIsPaidDocument(String username, Long documentId);
    void handleStripeCheckoutCompleted(String stripeSessionId, String paymentId);
}
