package com.iprody.xpaymentadapterapp.async;

import com.iprody.paymentserviceapp.persistence.entity.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class XPaymentAdapterResponseMessage implements Message {
    private UUID messageId;
    private UUID paymentGuid;
    private BigDecimal amount;
    private Currency currency;
    private UUID transactionRefId;
    private XPaymentAdapterStatus status;
    private OffsetDateTime occurredAt;
}
