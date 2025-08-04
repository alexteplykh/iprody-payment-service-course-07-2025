package com.iprody.paymentserviceapp.controller;

import com.iprody.paymentserviceapp.persistence.entity.Currency;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PaymentFilterDto {
    private PaymentStatus status;
    private Currency currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Instant createdAfter;
    private Instant createdBefore;
}
