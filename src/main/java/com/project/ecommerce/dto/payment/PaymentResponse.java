package com.project.ecommerce.dto.payment;

import com.project.ecommerce.enums.PaymentMethod;
import com.project.ecommerce.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {

    private Long id;

    private Long orderId;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;

    private BigDecimal amount;

    private String transactionId;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}