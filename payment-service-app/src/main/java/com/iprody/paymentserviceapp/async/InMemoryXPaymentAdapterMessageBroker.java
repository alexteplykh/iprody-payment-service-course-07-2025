package com.iprody.paymentserviceapp.async;

import com.iprody.paymentserviceapp.controller.PaymentController;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(InMemoryXPaymentAdapterMessageBroker.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Autowired
    public InMemoryXPaymentAdapterMessageBroker(AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        UUID txId = UUID.randomUUID();
        scheduler.schedule(() -> emit(request, txId), 30, TimeUnit.SECONDS);

        log.info("Sent message of payment: {}", request.getPaymentGuid());
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId) {
        XPaymentAdapterStatus status;
        if (request.getAmount().stripTrailingZeros().scale() > 0) {
            status = XPaymentAdapterStatus.CANCELED;
        } else {
            status = request.getAmount().toBigIntegerExact().mod(BigInteger.TWO).equals(BigInteger.ZERO)
                    ? XPaymentAdapterStatus.SUCCEEDED
                    : XPaymentAdapterStatus.CANCELED;
        }

        XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();
        result.setPaymentGuid(request.getPaymentGuid());
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());
        result.setTransactionRefId(txId);
        result.setStatus(status);
        result.setOccurredAt(OffsetDateTime.now());

        resultListener.onMessage(result);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
