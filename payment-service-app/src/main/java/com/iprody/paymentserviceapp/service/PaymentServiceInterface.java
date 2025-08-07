package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.PaymentFilterDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentServiceInterface {
    PaymentDto get(UUID id);
    Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable);
    PaymentDto create(PaymentDto dto);
    PaymentDto update(UUID id, PaymentDto dto);
    void delete(UUID id);
    PaymentDto updateStatus(UUID id, @NotNull PaymentStatus status);
}
