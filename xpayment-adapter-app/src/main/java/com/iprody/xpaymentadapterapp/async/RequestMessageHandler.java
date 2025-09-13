package com.iprody.xpaymentadapterapp.async;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private final AsyncSender<XPaymentAdapterResponseMessage> sender;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> sender) {
        this.sender = sender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        scheduler.schedule(() -> {
            XPaymentAdapterStatus status;
            if (message.getAmount().stripTrailingZeros().scale() > 0) {
                status = XPaymentAdapterStatus.CANCELED;
            } else {
                status = message.getAmount().toBigIntegerExact().mod(BigInteger.TWO).equals(BigInteger.ZERO)
                        ? XPaymentAdapterStatus.SUCCEEDED
                        : XPaymentAdapterStatus.CANCELED;
            }

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(status);
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());
            sender.send(responseMessage);
        }, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
