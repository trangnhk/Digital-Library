/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.trangnhk.dto.CreatePaymentRequestDTO;
import com.trangnhk.dto.PaymentResponseDTO;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.Notification;
import com.trangnhk.pojo.Payment;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.PaymentMethod;
import com.trangnhk.pojo.enums.PaymentStatus;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.repositories.NotificationRepository;
import com.trangnhk.repositories.PaymentRepository;
import com.trangnhk.services.PaymentService;
import com.trangnhk.services.UserService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private static final String NOTIFICATION_PAYMENT_TITLE = "Successfully Paid";
    private static final String NOTIFICATION_PAYMENT_CONTENT = "You has successfully paid at ";

    @Autowired
    private UserService userService;
    @Autowired
    private DocumentRepository docRepo;
    @Autowired
    private PaymentRepository paymentRepo;
    @Autowired
    private NotificationRepository notiRepo;
    
    

    @Value("${stripe.secret_key}")
    private String stripeSecretKey;
    @Value("${stripe.success_url}")
    private String stripeSuccessUrl;
    @Value("${stripe.cancel_url}")
    private String stripeCancelUrl;
    @Value("${stripe.currency}")
    private String stripeCurrency;

    @Override
    public PaymentResponseDTO createPayment(String username, CreatePaymentRequestDTO dto) {

        User currentU = this.userService.getUserByUsername(username);

        if (currentU == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        PaymentMethod method = this.parsePaymentMethod(dto.getPaymentMethod());
        Document doc = this.docRepo.getDocumentById(dto.getDocumentId());

        if (doc == null || Boolean.TRUE.equals(doc.getDeleted()) || Boolean.FALSE.equals(doc.getApproved())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");
        }

        if (Boolean.FALSE.equals(doc.getPremium())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Free document does not require payment");
        }

        if (this.paymentRepo.existSuccessPayment(currentU.getId(), doc.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already paid for this document");
        }

        if (doc.getPrice() == null || doc.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Premium document price must be more than 0");
        }

        Payment payment = new Payment();
        payment.setUser(currentU);
        payment.setDocument(doc);
        payment.setAmount(doc.getPrice());
        payment.setPaymentMethod(method);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setDescription("Payment for document: " + doc.getId());

        if (method == PaymentMethod.CASH) {
            Payment savedPayment = this.paymentRepo.add(payment);
            return PaymentResponseDTO.fromPayment(savedPayment);
        }

        if (method == PaymentMethod.STRIPE) {
            payment.setCurrency(this.stripeCurrency.trim().toLowerCase());

            System.out.println("stripeCurrency = [" + this.stripeCurrency + "]");
            System.out.println("stripeCurrency length = " + this.stripeCurrency.length());
            System.out.println("payment.currency = [" + payment.getCurrency() + "]");
            System.out.println("payment.currency length = " + payment.getCurrency().length());

            Payment savedPayment = this.paymentRepo.add(payment);

            Session stripeSession = this.createStripeCheckoutSession(savedPayment, doc);

            savedPayment.setStripeSessionId(stripeSession.getId());
            savedPayment.setCheckoutUrl(stripeSession.getUrl());

            Payment updatedPayment = this.paymentRepo.update(savedPayment);

            return PaymentResponseDTO.fromPayment(updatedPayment);
        }

        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported payment method");

    }

    private Session createStripeCheckoutSession(Payment payment, Document document) {
        try {
            Stripe.apiKey = this.stripeSecretKey;

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
                                                    .setCurrency(this.stripeCurrency)
                                                    .setUnitAmount(amount)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(document.getTitle())
                                                                    .setDescription("Digital document access")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            return Session.create(params);

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

        if ("usd".equalsIgnoreCase(this.stripeCurrency)) {
            return Math.round(price * 100);
        }

        if ("vnd".equalsIgnoreCase(this.stripeCurrency)) {
            return price.longValue();
        }

        return Math.round(price * 100);
    }

    private PaymentMethod parsePaymentMethod(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment method is required");
        }

        try {
            return PaymentMethod.valueOf(value.trim().toUpperCase());

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Payment method must be CASH or STRIPE");
        }
    }

    @Override
    public void handleStripeCheckoutCompleted(String stripeSessionId, String paymentId) {
        Payment payment = this.paymentRepo.getPaymentByStripeSessionBy(stripeSessionId);

        if (payment == null) {
            return;
        }

        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(new Date());
        payment.setStripePaymentIntentId(paymentId);
        payment.setTransactionCode(paymentId);
        
        this.paymentRepo.update(payment);
        
        Notification noti = new Notification();
        noti.setUser(payment.getUser());
        noti.setTitle(NOTIFICATION_PAYMENT_TITLE);
        noti.setContent(NOTIFICATION_PAYMENT_CONTENT);
        noti.setIsRead(Boolean.FALSE);
        noti.setCreatedDate(payment.getPaymentDate());
        
        Notification savedNoti = this.notiRepo.save(noti);
        
    }

    @Override
    public boolean checkIsPaidDocument(String username, Long documentId) {
       User currentU = this.userService.getUserByUsername(username);
       
       if (currentU == null){
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
       }
       
       return Boolean.TRUE.equals(this.paymentRepo.existSuccessPayment(currentU.getId(), documentId));
       
       
    }

}
