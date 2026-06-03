/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.payments;

import com.trangnhk.pojo.enums.PaymentMethod;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */

@Component
public class PaymentProcessorFactory {
    private final Map<PaymentMethod, PaymentProcessor> processorMap = new HashMap<>();
    
    @Autowired
    private List<PaymentProcessor> processors;
    
    @PostConstruct
    public void init(){
        for(PaymentProcessor processor: processors){
            this.processorMap.put(processor.getPaymentMethod(), processor);
        }
    }
    
    public PaymentProcessor getProcessor(PaymentMethod method){
        PaymentProcessor processor = this.processorMap.get(method);
        
        if (processor == null){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported payment method");
        }
        
        return processor;

    }
    
}
