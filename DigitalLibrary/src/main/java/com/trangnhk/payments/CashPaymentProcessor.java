/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.payments;

import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.Payment;
import com.trangnhk.pojo.enums.PaymentMethod;
import org.springframework.stereotype.Component;

/**
 *
 * @author Admin
 */

@Component
public class CashPaymentProcessor implements PaymentProcessor{

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CASH;
    }

    @Override
    public PaymentGatewayResult createPayment(Payment payment, Document document) {
        return new PaymentGatewayResult();
    }
    
}
