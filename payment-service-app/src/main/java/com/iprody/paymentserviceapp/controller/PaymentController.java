package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.service.PaymentServiceInterface;
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

    private final PaymentServiceInterface paymentService;

    @GetMapping("/{guid}")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        return paymentService.get(guid);
    }

    @GetMapping("/search")
    public Page<PaymentDto> getAllPayment(@ModelAttribute PaymentFilterDto paymentFilter,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "25") int size,
                                          @RequestParam(defaultValue = "createdAt") String sortedBy,
                                          @RequestParam(defaultValue = "desc") String direction) {

        return paymentService.search(paymentFilter, page, size, sortedBy, direction);
    }
}
