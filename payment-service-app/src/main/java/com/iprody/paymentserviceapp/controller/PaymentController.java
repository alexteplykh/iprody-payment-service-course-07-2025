package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.model.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final Map<Long, Payment> paymentMap = Map.of(
            1L, new Payment(1L, 23.5),
            2L, new Payment(2L, 107.4),
            3L, new Payment(3L, 41.8),
            4L, new Payment(4L, 94.7),
            5L, new Payment(5L, 36.7)
    );

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable Long id) {
        return paymentMap1.get(id);
    }

    @GetMapping
    public List<Payment> getAllPayment() {
        return paymentMap.values().stream().toList();
    }
}
