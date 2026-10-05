package com.project.ecommerce.controller;

import com.project.ecommerce.dto.payment.PaymentRequest;
import com.project.ecommerce.dto.payment.PaymentResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // =========================
    // CREATE PAYMENT
    // =========================

    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                paymentService.createPayment(
                        user.getId(),
                        orderId,
                        request
                )
        );
    }

    // =========================
    // CASH ON DELIVERY
    // =========================

    @PostMapping("/order/{orderId}/cod")
    public ResponseEntity<PaymentResponse>
    confirmCashOnDelivery(
            @PathVariable Long orderId,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                paymentService.confirmCashOnDelivery(
                        user.getId(),
                        orderId
                )
        );
    }

    // =========================
    // GET MY PAYMENT
    // =========================

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long orderId,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                paymentService.getPayment(
                        user.getId(),
                        orderId
                )
        );
    }

    // =========================
    // ADMIN: MARK PAID
    // =========================

    @PutMapping("/order/{orderId}/paid")
    public ResponseEntity<PaymentResponse> markAsPaid(
            @PathVariable Long orderId,
            @RequestParam String transactionId) {

        return ResponseEntity.ok(
                paymentService.markAsPaid(
                        orderId,
                        transactionId
                )
        );
    }

    // =========================
    // ADMIN: MARK FAILED
    // =========================

    @PutMapping("/order/{orderId}/failed")
    public ResponseEntity<PaymentResponse> markAsFailed(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.markAsFailed(
                        orderId
                )
        );
    }
}