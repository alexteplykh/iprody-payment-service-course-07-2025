package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.PaymentFilterDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Currency;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid;

    @BeforeEach
    void setUp() {
        guid = UUID.randomUUID();

        payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency(Currency.USD);
        payment.setTransactionRefId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setNote("note");
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());

        paymentDto = new PaymentDto();
        paymentDto.setGuid(payment.getGuid());
        paymentDto.setInquiryRefId(payment.getInquiryRefId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setTransactionRefId(payment.getTransactionRefId());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setNote(payment.getNote());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());
    }

    @Test
    void get_validId_returnsDto() {
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //given
        //when
        PaymentDto result = paymentService.get(guid);

        //then
        assertEquals(paymentDto.getGuid(), result.getGuid());
        assertEquals(paymentDto.getInquiryRefId(), result.getInquiryRefId());
        assertEquals(paymentDto.getAmount(), result.getAmount());
        assertEquals(paymentDto.getCurrency(), result.getCurrency());
        assertEquals(paymentDto.getTransactionRefId(), result.getTransactionRefId());
        assertEquals(paymentDto.getStatus(), result.getStatus());
        assertEquals(paymentDto.getNote(), result.getNote());
        assertEquals(paymentDto.getCreatedAt(), result.getCreatedAt());
        assertEquals(paymentDto.getUpdatedAt(), result.getUpdatedAt());

        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void get_notValidId_throwsException() {
        //given
        when(paymentRepository.findById(guid)).thenReturn(Optional.empty());

        //when-then
        assertThrows(EntityNotFoundException.class, () -> {
            paymentService.get(guid);
        });

        //when
        verify(paymentRepository).findById(guid);
    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void search_byPaymentStatus_returnsDto(PaymentStatus status) {
        try (MockedStatic<PaymentFilterFactory> mocked = mockStatic(PaymentFilterFactory.class)) {
            //given
            payment.setStatus(status);
            paymentDto.setStatus(status);
            int page = 0;
            int size = 5;
            String sortedBy = "guid";
            String direction = "desc";

            PaymentFilterDto filter = new PaymentFilterDto();
            filter.setStatus(status);

            Sort sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortedBy).descending()
                    : Sort.by(sortedBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);

            mocked.when(() -> PaymentFilterFactory.fromFilter(filter)).thenReturn(spec);
            when(paymentRepository.findAll(spec, pageable)).thenReturn(new PageImpl<>(List.of(payment), pageable, 1));
            when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

            //when
            Page<PaymentDto> result = paymentService.search(filter, pageable);

            //then
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(paymentDto.getStatus(), result.getContent().getFirst().getStatus());
            assertEquals(paymentDto.getStatus(), result.getContent().getFirst().getStatus());

            verify(paymentRepository).findAll(spec, pageable);
            verify(paymentMapper).toDto(payment);
        }
    }

    @Test
    void search_byFilter_returnsDto() {
        try (MockedStatic<PaymentFilterFactory> mocked = mockStatic(PaymentFilterFactory.class)) {
            //given
            int page = 0;
            int size = 5;
            String sortedBy = "guid";
            String direction = "desc";

            PaymentFilterDto filter = new PaymentFilterDto();
            filter.setStatus(PaymentStatus.APPROVED);
            filter.setCurrency(Currency.USD);
            filter.setMinAmount(new BigDecimal("90"));
            filter.setMaxAmount(new BigDecimal("110"));

            Sort sort = direction.equalsIgnoreCase("desc")
                    ? Sort.by(sortedBy).descending()
                    : Sort.by(sortedBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);

            mocked.when(() -> PaymentFilterFactory.fromFilter(filter)).thenReturn(spec);
            when(paymentRepository.findAll(spec, pageable)).thenReturn(new PageImpl<>(List.of(payment), pageable, 1));
            when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

            //when
            Page<PaymentDto> result = paymentService.search(filter, pageable);

            //then
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(paymentDto.getStatus(), result.getContent().getFirst().getStatus());
            assertEquals(paymentDto.getStatus(), result.getContent().getFirst().getStatus());

            verify(paymentRepository).findAll(spec, pageable);
            verify(paymentMapper).toDto(payment);
        }
    }
}