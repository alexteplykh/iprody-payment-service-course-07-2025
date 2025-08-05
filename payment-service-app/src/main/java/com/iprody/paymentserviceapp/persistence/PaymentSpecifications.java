package com.iprody.paymentserviceapp.persistence;

import com.iprody.paymentserviceapp.persistence.entity.Currency;
import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class PaymentSpecifications {

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Payment> hasCurrency(Currency currency) {
        return (root, query, cb) ->
                cb.equal(root.get("currency"), currency);
    }

    public static Specification<Payment> amountLessThan(BigDecimal min) {
        return (root, query, cb) ->
                cb.lt(root.get("amount"), min);
    }

    public static Specification<Payment> amountGreaterThan(BigDecimal max) {
        return (root, query, cb) ->
                cb.gt(root.get("amount"), max);
    }

    public static Specification<Payment> amountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) ->
                cb.between(root.get("amount"), min, max);
    }

    public static Specification<Payment> createdAtBefore(OffsetDateTime before) {
        return (root, query, cb) ->
                cb.lessThan(root.get("createdAt"), before);
    }

    public static Specification<Payment> createdAtAfter(OffsetDateTime after) {
        return (root, query, cb) ->
                cb.greaterThan(root.get("createdAt"), after);
    }

    public static Specification<Payment> createdBetween(OffsetDateTime after, OffsetDateTime before) {
        return (root, query, cb) ->
                cb.between(root.get("createdAt"), after, before);
    }
}
