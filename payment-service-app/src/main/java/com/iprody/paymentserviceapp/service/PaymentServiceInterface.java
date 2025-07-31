package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.PaymentFilterDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentServiceInterface {
    PaymentDto get(UUID id);
    Page<PaymentDto> search(PaymentFilterDto filter, int page, int size, String sortedBy, String direction);
}
