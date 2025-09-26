package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeResponseDto;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {
    CreateChargeResponseDto createCharge(CreateChargeRequestDto createChargeRequest) throws RestClientException;
    CreateChargeResponseDto retrieveCharge(UUID id) throws RestClientException;
}
