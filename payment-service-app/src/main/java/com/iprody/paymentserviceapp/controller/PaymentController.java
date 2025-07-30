package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/{guid}")
    public Payment getPayment(@PathVariable UUID guid) {
        return paymentRepository.findById(guid).orElse(null);
    }

    @GetMapping("/search")
    public List<Payment> getAllPayment(@ModelAttribute PaymentFilterDto paymentFilter) {
        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter));
    }
}
