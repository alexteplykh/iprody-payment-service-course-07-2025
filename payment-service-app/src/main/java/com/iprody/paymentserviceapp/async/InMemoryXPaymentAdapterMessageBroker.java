package com.iprody.paymentserviceapp.async;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Autowired
    public InMemoryXPaymentAdapterMessageBroker(AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        UUID txId = UUID.randomUUID();
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.PROCESSING), 0, TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.PROCESSING), 10, TimeUnit.SECONDS);
        scheduler.schedule(() -> emit(request, txId, XPaymentAdapterStatus.SUCCEEDED), 20, TimeUnit.SECONDS);
    }

    private void emit(XPaymentAdapterRequestMessage request, UUID txId, XPaymentAdapterStatus status) {
        XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();
        result.setPaymentGuid(request.getMessageId());
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
