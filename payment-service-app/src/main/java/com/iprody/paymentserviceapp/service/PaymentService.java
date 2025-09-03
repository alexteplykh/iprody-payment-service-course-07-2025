package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.async.AsyncSender;
import com.iprody.paymentserviceapp.async.XPaymentAdapterRequestMessage;
import com.iprody.paymentserviceapp.controller.PaymentFilterDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.exception.EntityNotFoundException;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.mapper.XPaymentAdapterMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService implements PaymentServiceInterface {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final XPaymentAdapterMapper xPaymentAdapterMapper;
    private final AsyncSender<XPaymentAdapterRequestMessage> sender;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper,
                          XPaymentAdapterMapper xPaymentAdapterMapper,
                          AsyncSender<XPaymentAdapterRequestMessage> sender) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.xPaymentAdapterMapper = xPaymentAdapterMapper;
        this.sender = sender;
    }

    @Override
    public PaymentDto get(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id, "get", "Платеж не найден"));
    }

    @Override
    public Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);

        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = paymentMapper.toEntity(dto);
        Payment saved = paymentRepository.save(entity);

        PaymentDto resultDto = paymentMapper.toDto(saved);

        XPaymentAdapterRequestMessage requestMessage = xPaymentAdapterMapper.toXPaymentAdapterRequestMessage(entity);
        sender.send(requestMessage);

        return resultDto;
    }

    @Override
    public PaymentDto update(UUID id, PaymentDto dto) {
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Платеж не найден" + id);
        }

        Payment updated = paymentMapper.toEntity(dto);
        updated.setGuid(id);
        Payment saved = paymentRepository.save(updated);
        return paymentMapper.toDto(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException(id, "delete", "Платеж не найден");
        }
        paymentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        int updatedRows = paymentRepository.updateStatus(id, status);

        if (updatedRows == 0) {
            throw new EntityNotFoundException(id, "updateStatus", "Платеж не найден");
        }

        Payment updated = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(id, "updateStatus", "Платеж не найден после обновления"));

        return paymentMapper.toDto(updated);
    }

    @Override
    public PaymentDto updateNote(UUID id, String note) {
        int updatedRows = paymentRepository.updateNote(id, note);

        if (updatedRows == 0) {
            throw new EntityNotFoundException(id, "updateNote", "Платеж не найден");
        }

        Payment updated = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, "updateNote", "Платеж не найден после обновления"));

        return paymentMapper.toDto(updated);
    }


}
