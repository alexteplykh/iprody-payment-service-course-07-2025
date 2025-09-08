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
        String operation = "Create payment";
        logPaymentOperation(operation, dto.getGuid());

        PaymentDto createdDto = paymentService.create(dto);

        logPaymentData(operation, createdDto == null ? "Not created" : "Created");

        return createdDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{guid}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public PaymentDto getPayment(@PathVariable UUID guid) {
        String operation = "Get payment";
        logPaymentOperation(operation, guid);

        PaymentDto paymentDto = paymentService.get(guid);

        logPaymentData(operation, paymentDto == null ? "Not received" : "Received");
        return paymentDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    public Page<PaymentDto> getAllPayment(@ModelAttribute PaymentFilterDto paymentFilter,
                                          Pageable pageable) {
        String operation = "Get all payments";
        logPaymentOperation(operation, null);

        Page<PaymentDto> paymentDtoPage = paymentService.search(paymentFilter, pageable);

        logPaymentData(operation, paymentDtoPage == null ? "Not received" : "Received");
        return paymentDtoPage;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('admin')")
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto dto) {
        String operation = "Update payment";
        logPaymentOperation(operation, id);

        PaymentDto updatedDto = paymentService.update(id, dto);

        logPaymentData(operation, updatedDto == null ? "Not updated" : "Updated");
        return updatedDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID id) {
        String operation = "Delete payment";
        logPaymentOperation(operation, id);

        paymentService.delete(id);

        logPaymentData(operation, "Deleted");
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateStatus(@PathVariable UUID id,
                                   @RequestBody @Valid PaymentStatusUpdateDto dto) {
        String operation = "Update payment status";
        logPaymentOperation(operation, id);

        PaymentDto updatedDto = paymentService.updateStatus(id, dto.getStatus());

        logPaymentData(operation, updatedDto == null ? "Not updated" : "Updated");
        return updatedDto;
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('admin')")
    public PaymentDto updateNote(@PathVariable UUID id,
                                 @RequestBody @Valid NoteUpdateDto dto) {
        String operation = "Update payment note";
        logPaymentOperation(operation, id);

        PaymentDto updatedDto = paymentService.updateNote(id, dto.getNote());

        logPaymentData(operation, updatedDto == null ? "Not updated" : "Updated");
        return paymentService.updateNote(id, dto.getNote());
    }

    private void logPaymentOperation(String operation, UUID id) {
        log.info("{} : {}", operation, id);
    }

    private void logPaymentData(String operation, String state) {
        log.debug("{} : {} ", operation, state);
    }
}
