/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.payments;

/**
 *
 * @author Admin
 */
public class PaymentGatewayResult {
    private String checkoutUrl;
    private String stripeSessionId;
    private String transactionCode;

    public PaymentGatewayResult() {
    }

    public PaymentGatewayResult(String checkoutUrl, String stripeSessionId, String transactionCode) {
        this.checkoutUrl = checkoutUrl;
        this.stripeSessionId = stripeSessionId;
        this.transactionCode = transactionCode;
    }

    /**
     * @return the checkoutUrl
     */
    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    /**
     * @param checkoutUrl the checkoutUrl to set
     */
    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    /**
     * @return the stripeSessionId
     */
    public String getStripeSessionId() {
        return stripeSessionId;
    }

    /**
     * @param stripeSessionId the stripeSessionId to set
     */
    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    /**
     * @return the transactionCode
     */
    public String getTransactionCode() {
        return transactionCode;
    }

    /**
     * @param transactionCode the transactionCode to set
     */
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
    
}
