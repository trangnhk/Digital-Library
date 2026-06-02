/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories;

import com.trangnhk.pojo.Payment;

/**
 *
 * @author Admin
 */
public interface PaymentRepository {
    boolean existsByDocumentId(Long documentId);
    boolean existSuccessPayment(Long userId, Long documentId);
    Payment getPaymentById(Long id);
    Payment getPaymentByStripeSessionBy(String stripeSessionId);
    
    Payment add(Payment payment);
    Payment update(Payment payment);
    
    
}
