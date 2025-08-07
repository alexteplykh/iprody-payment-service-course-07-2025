package com.iprody.paymentserviceapp.dto;

import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteUpdateDto {

    @NotNull
    private String note;
}
