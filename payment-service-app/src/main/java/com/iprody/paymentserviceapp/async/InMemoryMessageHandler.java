package com.iprody.paymentserviceapp.async;

import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.iprody.paymentserviceapp.persistence.entity.PaymentStatus.*;

@Component
public class InMemoryMessageHandler implements MessageHandler<XPaymentAdapterResponseMessage> {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMessageHandler.class);

    private final PaymentRepository paymentRepository;

    public InMemoryMessageHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        PaymentStatus newStatus = switch (message.getStatus()) {
            case PROCESSING -> PENDING;
            case CANCELED   -> DECLINED;
            case SUCCEEDED  -> APPROVED;
        };

        paymentRepository.updateStatus(message.getPaymentGuid(), newStatus);

        log.info("Payment status updated for payment: " + message.getPaymentGuid());
    }
}
