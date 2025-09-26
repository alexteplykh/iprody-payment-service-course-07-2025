package com.iprody.xpaymentadapterapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateChargeRequestDto {
    private BigDecimal amount;
    private String currency;
    private String customer;
    private UUID order;
    private String receiptEmail;
}
