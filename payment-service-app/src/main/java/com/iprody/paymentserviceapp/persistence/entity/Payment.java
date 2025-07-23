package com.iprody.paymentserviceapp.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private UUID id;
    private UUID inquiryRefId;
    private BigDecimal amount;
    private String currency;
    private UUID transactionReadId;
    private PaymentStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Payment() {

    }
}
