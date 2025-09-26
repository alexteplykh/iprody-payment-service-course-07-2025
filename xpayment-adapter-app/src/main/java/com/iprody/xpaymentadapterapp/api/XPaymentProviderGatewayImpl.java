package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi) {
        this.defaultApi = defaultApi;
    }

    @Override
    public ChargeResponse createCharge(CreateChargeRequest createChargeRequest) throws RestClientException {
        return defaultApi.createCharge(createChargeRequest);
    }

    @Override
    public ChargeResponse retrieveCharge(UUID id) throws RestClientException {
        return defaultApi.retrieveCharge(id);
    }
}
