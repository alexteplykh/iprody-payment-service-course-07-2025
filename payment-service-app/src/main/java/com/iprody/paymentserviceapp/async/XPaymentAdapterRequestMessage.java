package com.iprody.paymentserviceapp.async;

import com.iprody.paymentserviceapp.persistence.entity.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class XPaymentAdapterRequestMessage implements Message {

    @Setter
    private UUID paymentGuid;

    @Setter
    @Getter
    private BigDecimal amount;

    @Setter
    @Getter
    private Currency currency;

    @Setter
    private OffsetDateTime occurredAt;

    @Override
    public UUID getMessageId() {
        return paymentGuid;
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
