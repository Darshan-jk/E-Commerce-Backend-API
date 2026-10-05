package com.project.ecommerce.controller;

import com.project.ecommerce.dto.order.OrderRequest;
import com.project.ecommerce.dto.order.OrderResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.project.ecommerce.enums.PaymentStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // =========================
    // PLACE ORDER
    // =========================

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                orderService.placeOrder(
                        user.getId(),
                        request
                )
        );
    }

    // =========================
    // GET MY ORDERS
    // =========================

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                orderService.getUserOrders(
                        user.getId()
                )
        );
    }

    // =========================
    // GET MY ORDER
    // =========================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                orderService.getUserOrder(
                        user.getId(),
                        orderId
                )
        );
    }

    // =========================
    // CANCEL MY ORDER
    // =========================

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                orderService.cancelOrder(
                        user.getId(),
                        orderId
                )
        );
    }

    // =========================
    // ADMIN: UPDATE STATUS
    // =========================

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                orderService.updateStatus(
                        orderId,
                        status
                )
        );
    }

    // =========================
    // ADMIN: GET BY STATUS
    // =========================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status) {

        return ResponseEntity.ok(
                orderService.getOrdersByStatus(
                        status
                )
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.getOrderById(orderId)
        );
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/payment-status/{status}")
    public ResponseEntity<List<OrderResponse>>
    getOrdersByPaymentStatus(
            @PathVariable PaymentStatus status) {

        return ResponseEntity.ok(
                orderService.getOrdersByPaymentStatus(
                        status
                )
        );
    }
}