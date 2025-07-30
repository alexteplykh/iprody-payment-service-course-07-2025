package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/{guid}")
    public Payment getPayment(@PathVariable UUID guid) {
        return paymentRepository.findById(guid).orElse(null);
    }

    @GetMapping("/search")
    public Page<Payment> getAllPayment(@ModelAttribute PaymentFilterDto paymentFilter,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size,
                                       @RequestParam(defaultValue = "createdAt") String sortedBy,
                                       @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortedBy).descending()
                : Sort.by(sortedBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return paymentRepository.findAll(PaymentFilterFactory.fromFilter(paymentFilter), pageable);
    }
}
