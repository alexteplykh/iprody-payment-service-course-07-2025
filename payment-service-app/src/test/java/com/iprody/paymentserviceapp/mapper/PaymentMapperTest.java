package com.iprody.paymentserviceapp.mapper;

import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.persistence.entity.Currency;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMapperTest {
    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void toDto_validEntity_returnsValidDto() {
        //given
        UUID guid = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        String note = "note";
        Payment payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(inquiryRefId);
        payment.setAmount(new BigDecimal("123.45"));
        payment.setCurrency(Currency.USD);
        payment.setTransactionRefId(transactionRefId);
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setNote(note);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());

        //when
        PaymentDto dto = mapper.toDto(payment);

        //then
        assertThat(dto).isNotNull();
        assertThat(dto.getGuid()).isEqualTo(payment.getGuid());
        assertThat(dto.getInquiryRefId()).isEqualTo(payment.getInquiryRefId());
        assertThat(dto.getAmount()).isEqualTo(payment.getAmount());
        assertThat(dto.getCurrency()).isEqualTo(payment.getCurrency());
        assertThat(dto.getTransactionRefId()).isEqualTo(payment.getTransactionRefId());
        assertThat(dto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(dto.getNote()).isEqualTo(payment.getNote());
        assertThat(dto.getCreatedAt()).isEqualTo(payment.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(payment.getUpdatedAt());
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        //given
        Payment payment = null;

        //when
        PaymentDto dto = mapper.toDto(payment);

        //then
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_validDto_returnsValidEntity() {
        //given
        UUID guid = UUID.randomUUID();
        UUID inquiryRefId = UUID.randomUUID();
        UUID transactionRefId = UUID.randomUUID();
        String note = "note";
        OffsetDateTime now = OffsetDateTime.now();
        PaymentDto dto = new PaymentDto(
                guid,
                inquiryRefId,
                new BigDecimal("999.99"),
                Currency.EUR,
                transactionRefId,
                PaymentStatus.PENDING,
                note,
                now,
                now
        );

        //when
        Payment entity = mapper.toEntity(dto);

        //then
        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(dto.getGuid());
        assertThat(entity.getInquiryRefId()).isEqualTo(dto.getInquiryRefId());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getCurrency()).isEqualTo(dto.getCurrency());
        assertThat(entity.getTransactionRefId()).isEqualTo(dto.getTransactionRefId());
        assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
        assertThat(entity.getNote()).isEqualTo(dto.getNote());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(dto.getUpdatedAt());
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        //given
        PaymentDto dto = null;

        //when
        Payment entity = mapper.toEntity(dto);

        //then
        assertThat(entity).isNull();
    }
}