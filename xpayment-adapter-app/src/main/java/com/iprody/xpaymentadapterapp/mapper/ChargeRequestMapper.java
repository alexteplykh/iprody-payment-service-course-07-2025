package com.iprody.xpaymentadapterapp.mapper;

import com.iprody.xpayment.app.api.model.CreateChargeRequest;
import com.iprody.xpaymentadapterapp.dto.CreateChargeRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeRequestMapper {
    CreateChargeRequest toProviderRequest(CreateChargeRequestDto providerRequest);
    CreateChargeRequestDto toAdapterRequest(CreateChargeRequest dto);
}
