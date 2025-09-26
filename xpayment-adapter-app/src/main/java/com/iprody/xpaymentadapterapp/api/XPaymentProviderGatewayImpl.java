package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpayment.app.api.client.DefaultApi;
import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeResponseDto;
import com.iprody.xpaymentadapterapp.mapper.ChargeRequestMapper;
import com.iprody.xpaymentadapterapp.mapper.ChargeResponseMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;
    private final ChargeRequestMapper requestMapper;
    private final ChargeResponseMapper responseMapper;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi,
                                       ChargeRequestMapper requestMapper,
                                       ChargeResponseMapper responseMapper) {
        this.defaultApi = defaultApi;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @Override
    public CreateChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequest) throws RestClientException {
        ChargeResponse response = defaultApi.createCharge(requestMapper.toProviderRequest(createChargeRequest));
        return responseMapper.toAdapterResponse(response);
    }

    @Override
    public CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException {
        return responseMapper.toAdapterResponse(defaultApi.retrieveCharge(id));
    }
}
