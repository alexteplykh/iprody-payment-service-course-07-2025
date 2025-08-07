package com.iprody.paymentserviceapp.persistence;

import com.iprody.paymentserviceapp.persistence.entity.Payment;
import com.iprody.paymentserviceapp.persistence.entity.PaymentStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :status WHERE p.guid = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") PaymentStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.note = :note WHERE p.guid = :id")
    int updateNote(UUID id, String note);
}
