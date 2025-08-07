package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.service.PaymentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceInterface paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@RequestBody PaymentDto dto) {
        return paymentService.create(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{guid}")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        return paymentService.get(guid);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    public Page<PaymentDto> getAllPayment(@ModelAttribute PaymentFilterDto paymentFilter,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "25") int size,
                                          @RequestParam(defaultValue = "createdAt") String sortedBy,
                                          @RequestParam(defaultValue = "desc") String direction) {

        return paymentService.search(paymentFilter, page, size, sortedBy, direction);
    }

    @PutMapping("/{id}")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        return paymentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        paymentService.delete(id);
    }
}
