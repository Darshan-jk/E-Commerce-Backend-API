package com.project.ecommerce.dto.order;

import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;

    private Long addressId;
    private String deliveryName;
    private String deliveryPhone;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPostalCode;
    private String deliveryCountry;

    private OrderStatus status;
    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}