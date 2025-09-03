package com.iprody.paymentserviceapp.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryXPaymentAdapterResultListenerAdapter implements AsyncListener<XPaymentAdapterResponseMessage> {
    private static final Logger log = LoggerFactory.getLogger(InMemoryXPaymentAdapterResultListenerAdapter.class);

    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    public InMemoryXPaymentAdapterResultListenerAdapter(MessageHandler<XPaymentAdapterResponseMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterResponseMessage msg) {
        log.info("Received response message for payment: {}", msg.getPaymentGuid());
        handler.handle(msg);
    }
}
