package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.dto.NoteUpdateDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.dto.PaymentStatusUpdateDto;
import com.iprody.paymentserviceapp.service.PaymentServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentServiceInterface paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto create(@RequestBody PaymentDto dto) {
        logPaymentOperation("Create payment", dto.getGuid());
        return paymentService.create(dto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        logPaymentOperation("Get payment", guid);
        return paymentService.get(guid);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> getAllPayment(@ModelAttribute PaymentFilterDto paymentFilter,
                                          Pageable pageable) {
        logPaymentOperation("Get all payments", null);
        return paymentService.search(paymentFilter, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('admin')")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        logPaymentOperation("Update payment", id);
        return paymentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID id) {
        logPaymentOperation("Delete payment", id);
        paymentService.delete(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateStatus(@PathVariable UUID id,
                                   @RequestBody @Valid PaymentStatusUpdateDto dto) {
        logPaymentOperation("Update payment status", id);
        return paymentService.updateStatus(id, dto.getStatus());
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateNote(@PathVariable UUID id,
                                 @RequestBody @Valid NoteUpdateDto dto) {
        logPaymentOperation("Update payment note", id);
        return paymentService.updateNote(id, dto.getNote());
    }

    private void logPaymentOperation(String operation, UUID id) {
        log.info("{} : {}", operation, id);
    }
}
