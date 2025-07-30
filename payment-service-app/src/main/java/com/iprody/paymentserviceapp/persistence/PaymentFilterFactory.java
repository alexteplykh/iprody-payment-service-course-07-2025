package com.iprody.paymentserviceapp.persistence;

import com.iprody.paymentserviceapp.controller.PaymentFilterDto;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentFilterFactory {
    private static final Specification<Payment> EMPTY = (root, query, criteriaBuilder) -> null;

    public static Specification<Payment> fromFilter(PaymentFilterDto filter) {
        Specification<Payment> spec = EMPTY;

        if (filter.getCurrency() != null) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.getCurrency()));
        }

        if (filter.getStatus() != null) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.getStatus()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() == null) {
            spec = spec.and(PaymentSpecifications.amountLessThan(filter.getMinAmount()));
        }

        if (filter.getMinAmount() == null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountGreaterThan(filter.getMaxAmount()));
        }

        if (filter.getMinAmount() != null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(filter.getMinAmount(), filter.getMaxAmount()));
        }

        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() == null) {
            spec = spec.and(PaymentSpecifications.createdAtAfter(filter.getCreatedAfter()));
        }

        if (filter.getCreatedAfter() == null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdAtBefore(filter.getCreatedBefore()));
        }

        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(filter.getCreatedAfter(), filter.getCreatedBefore()));
        }

        return spec;
    }
}
