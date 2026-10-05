package com.project.ecommerce.repository;

import com.project.ecommerce.entity.Order;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUserId(
            Long id,
            Long userId
    );

    List<Order> findByStatusOrderByCreatedAtDesc(
            OrderStatus status
    );

    List<Order> findByPaymentStatusOrderByCreatedAtDesc(
            PaymentStatus paymentStatus
    );

    List<Order> findAllByOrderByCreatedAtDesc();
}