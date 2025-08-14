package com.iprody.paymentserviceapp.service;

import com.iprody.paymentserviceapp.controller.PaymentFilterDto;
import com.iprody.paymentserviceapp.dto.PaymentDto;
import com.iprody.paymentserviceapp.mapper.PaymentMapper;
import com.iprody.paymentserviceapp.persistence.PaymentFilterFactory;
import com.iprody.paymentserviceapp.persistence.PaymentRepository;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService implements PaymentServiceInterface{
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentDto get(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден: " + id));
    }

    @Override
    public Page<PaymentDto> search(PaymentFilterDto filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);

        return paymentRepository.findAll(spec, pageable).map(paymentMapper::toDto);
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = paymentMapper.toEntity(dto);
        entity.setGuid(null);
        Payment saved = paymentRepository.save(entity);
        return paymentMapper.toDto(saved);
    }

    @Override
    public PaymentDto update(UUID id, PaymentDto dto) {
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Платеж не найден: " + id);
        }

        Payment updated = paymentMapper.toEntity(dto);
        updated.setGuid(id);
        Payment saved = paymentRepository.save(updated);
        return paymentMapper.toDto(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Платеж не найден: " + id);
        }
        paymentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        int updatedRows = paymentRepository.updateStatus(id, status);

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Платёж не найден: " + id);
        }

        Payment updated = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платёж не найден после обновления: " + id));

        return paymentMapper.toDto(updated);
    }

    @Override
    public PaymentDto updateNote(UUID id, String note) {
        int updatedRows = paymentRepository.updateNote(id, note);

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Платёж не найден: " + id);
        }

        Payment updated = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платёж не найден после обновления: " + id));

        return paymentMapper.toDto(updated);
    }


}
