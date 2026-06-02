/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import com.trangnhk.pojo.Payment;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class PaymentResponseDTO {
    private Long id;
    private Long documentId;
    private String documentTitle;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private String paymentStatus;
    private String checkoutUrl;
    private String stripeSessionId;
    private Date createdDate;
    private Date paymentDate;
    
    public static PaymentResponseDTO fromPayment(Payment payment){
        PaymentResponseDTO dto = new PaymentResponseDTO();
        
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setCheckoutUrl(payment.getCheckoutUrl());
        dto.setStripeSessionId(payment.getStripeSessionId());
        dto.setCreatedDate(payment.getCreatedDate());
        dto.setPaymentDate(payment.getPaymentDate());
        
        if (payment.getPaymentMethod() != null){
            dto.setPaymentMethod(payment.getPaymentMethod().name());
        }
        
        if (payment.getPaymentStatus()!= null){
            dto.setPaymentStatus(payment.getPaymentStatus().name());
        }
        
        if (payment.getDocument() != null){
            dto.setDocumentId(payment.getDocument().getId());
            dto.setDocumentTitle(payment.getDocument().getTitle());
        }
        
        return dto;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the documentId
     */
    public Long getDocumentId() {
        return documentId;
    }

    /**
     * @param documentId the documentId to set
     */
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    /**
     * @return the documentTitle
     */
    public String getDocumentTitle() {
        return documentTitle;
    }

    /**
     * @param documentTitle the documentTitle to set
     */
    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    /**
     * @return the amount
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the paymentMethod
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @param paymentMethod the paymentMethod to set
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return the paymentStatus
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * @param paymentStatus the paymentStatus to set
     */
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
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
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the paymentDate
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * @param paymentDate the paymentDate to set
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    
}
