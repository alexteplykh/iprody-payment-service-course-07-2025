package com.iprody.paymentserviceapp.async;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.UUID;

public class XPaymentAdapterResponseMessage implements Message {

    @Setter
    private UUID messageGuid;

    @Getter
    @Setter
    private UUID paymentGuid;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private Currency currency;

    @Getter
    @Setter
    private UUID transactionRefId;

    @Getter
    @Setter
    private XPaymentAdapterStatus status;

    @Getter
    @Setter
    private OffsetDateTime occurredAt;

    @Override
    public UUID getMessageId() {
        return messageGuid;
    }
}
