package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.persistence.entity.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final Map<Long, Payment> paymentMap = new HashMap<>();

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentMap.get(id);
    }

    @GetMapping
    public List<Payment> getAllPayment() {
        return paymentMap.values().stream().toList();
    }
}
