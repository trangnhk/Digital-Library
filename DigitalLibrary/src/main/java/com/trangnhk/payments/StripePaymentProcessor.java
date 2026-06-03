/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.payments;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.Payment;
import com.trangnhk.pojo.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */

@Component
public class StripePaymentProcessor implements PaymentProcessor{
    
    @Value("${stripe.secret_key}")
    private String stripeSecretKey;
    @Value("${stripe.success_url}")
    private String stripeSuccessUrl;
    @Value("${stripe.cancel_url}")
    private String stripeCancelUrl;
    @Value("${stripe.currency}")
    private String stripeCurrency;


    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.STRIPE;
    }

    @Override
    public PaymentGatewayResult createPayment(Payment payment, Document document) {
        try {
            Stripe.apiKey = this.stripeSecretKey;

            String currency = this.stripeCurrency.trim().toLowerCase();
            Long amount = this.resolveStripeAmount(payment.getAmount());

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(this.stripeSuccessUrl)
                    .setCancelUrl(this.stripeCancelUrl)
                    .putMetadata("paymentId", String.valueOf(payment.getId()))
                    .putMetadata("documentId", String.valueOf(document.getId()))
                    .putMetadata("userId", String.valueOf(payment.getUser().getId()))
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency(currency)
                                                    .setUnitAmount(amount)
                                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(document.getTitle())
                                                                    .setDescription("Digital document access")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            
            return new PaymentGatewayResult(session.getUrl(), session.getId(), null);

        } catch (StripeException ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stripe ERRROR: " + ex.getMessage());
        }
        catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Create Stripe checkout session failed");

        }
    }
    
    private Long resolveStripeAmount(Double price) {
        if (price == null || price <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Amount must be more than 0");
        }

        String currency = this.stripeCurrency.trim().toLowerCase(); 
        if ("vnd".equals(currency)) {
            if (price < 50000) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Stripe payment amount is too small. For VND, please set price at least 50000 VND");
            }

            return price.longValue();
        }
        
         if ("usd".equals(currency)) {
            if (price < 0.5) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Stripe payment amount is too small. Minimum amount is 0.50 USD");
            }

            return Math.round(price * 100);
        }

        return Math.round(price * 100);
    }

}
