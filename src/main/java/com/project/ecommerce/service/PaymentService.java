package com.project.ecommerce.service;

import com.project.ecommerce.dto.payment.PaymentRequest;
import com.project.ecommerce.dto.payment.PaymentResponse;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.Payment;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentMethod;
import com.project.ecommerce.enums.PaymentStatus;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // =========================
    // CREATE PAYMENT
    // =========================

    @Transactional
    public PaymentResponse createPayment(
            Long userId,
            Long orderId,
            PaymentRequest request) {

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Cannot make payment for cancelled order"
            );
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException(
                    "Order is already paid"
            );
        }

        if (paymentRepository
                .findByOrderId(orderId)
                .isPresent()) {

            throw new RuntimeException(
                    "Payment already created for this order"
            );
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .build();

        Payment savedPayment =
                paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    // =========================
    // COD PAYMENT
    // =========================

    @Transactional
    public PaymentResponse confirmCashOnDelivery(
            Long userId,
            Long orderId) {

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException(
                    "Cancelled order cannot be paid"
            );
        }

        if (paymentRepository
                .findByOrderId(orderId)
                .isPresent()) {

            throw new RuntimeException(
                    "Payment already exists"
            );
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.COD)
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .build();

        Payment savedPayment =
                paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    // =========================
    // ADMIN / SYSTEM:
    // MARK PAYMENT PAID
    // =========================

    @Transactional
    public PaymentResponse markAsPaid(
            Long orderId,
            String transactionId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"
                        ));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException(
                    "Payment is already marked as paid"
            );
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());

        Order order = payment.getOrder();

        order.setPaymentStatus(PaymentStatus.PAID);

        orderRepository.save(order);

        Payment updatedPayment =
                paymentRepository.save(payment);

        return toResponse(updatedPayment);
    }

    // =========================
    // ADMIN / SYSTEM:
    // MARK FAILED
    // =========================

    @Transactional
    public PaymentResponse markAsFailed(
            Long orderId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"
                        ));

        payment.setStatus(PaymentStatus.FAILED);

        Order order = payment.getOrder();

        order.setPaymentStatus(PaymentStatus.FAILED);

        orderRepository.save(order);

        Payment updatedPayment =
                paymentRepository.save(payment);

        return toResponse(updatedPayment);
    }

    // =========================
    // GET MY PAYMENT
    // =========================

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(
            Long userId,
            Long orderId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found"
                        ));

        if (!payment.getOrder()
                .getUser()
                .getId()
                .equals(userId)) {

            throw new RuntimeException(
                    "Payment does not belong to this user"
            );
        }

        return toResponse(payment);
    }

    // =========================
    // RESPONSE MAPPER
    // =========================

    private PaymentResponse toResponse(
            Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .paidAt(payment.getPaidAt())
                .build();
    }
}