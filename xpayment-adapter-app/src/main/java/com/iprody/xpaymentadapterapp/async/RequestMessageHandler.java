package com.iprody.xpaymentadapterapp.async;

import com.iprody.xpaymentadapterapp.api.XPaymentProviderGateway;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeResponseDto;
import com.iprody.xpaymentadapterapp.persistence.entity.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private static final Logger logger = LoggerFactory.getLogger(RequestMessageHandler.class);

    private final XPaymentProviderGateway xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    @Autowired
    public RequestMessageHandler(XPaymentProviderGateway xPaymentProviderGateway,
                                 AsyncSender<XPaymentAdapterResponseMessage> asyncSender) {
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.asyncSender = asyncSender;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        logger.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
                    message.getPaymentGuid(),
                    message.getAmount(),
                    message.getCurrency());

        CreateChargeRequestDto createChargeRequest = new CreateChargeRequestDto();
        createChargeRequest.setAmount(message.getAmount());
        createChargeRequest.setCurrency(message.getCurrency().toString());
        createChargeRequest.setOrder(message.getPaymentGuid());

        try {
            CreateChargeResponseDto chargeResponse = xPaymentProviderGateway.createCharge(createChargeRequest);

            logger.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                        chargeResponse.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(chargeResponse.getOrder());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setAmount(chargeResponse.getAmount());
            responseMessage.setCurrency(Currency.valueOf(chargeResponse.getCurrency()));
            responseMessage.setStatus(XPaymentAdapterStatus.valueOf(chargeResponse.getStatus()));
            responseMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(responseMessage);
        } catch (RestClientException ex) {
            logger.error("Error in time of sending payment request with paymentGuid - {}", message.getPaymentGuid(), ex);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(responseMessage);
        }
    }
}
