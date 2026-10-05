package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Payment;
import com.project.ecommerce.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByTransactionId(
            String transactionId
    );

    List<Payment> findByStatus(
            PaymentStatus status
    );
}