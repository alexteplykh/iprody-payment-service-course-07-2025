package com.iprody.xpaymentadapterapp.mapper;

import com.iprody.xpayment.app.api.model.ChargeResponse;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.dto.CreateChargeResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeResponseMapper {
    ChargeResponse toProviderResponse(CreateChargeRequestDto dto);
    CreateChargeResponseDto toAdapterResponse(ChargeResponse providerRequest);
}
