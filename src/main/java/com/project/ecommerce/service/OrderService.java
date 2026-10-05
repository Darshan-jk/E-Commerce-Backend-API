package com.project.ecommerce.service;

import com.project.ecommerce.dto.order.OrderItemResponse;
import com.project.ecommerce.dto.order.OrderRequest;
import com.project.ecommerce.dto.order.OrderResponse;
import com.project.ecommerce.entity.Address;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.Inventory;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.OrderItem;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.OrderStatus;
import com.project.ecommerce.enums.PaymentStatus;
import com.project.ecommerce.repository.AddressRepository;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.InventoryRepository;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final InventoryRepository inventoryRepository;

    // =========================
    // PLACE ORDER
    // =========================

    @Transactional
    public OrderResponse placeOrder(
            Long userId,
            OrderRequest request) {

        User user = getUser(userId);

        Address address = addressRepository
                .findByIdAndUserId(
                        request.getAddressId(),
                        userId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Address not found"
                        ));

        Cart cart = cartRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart not found"
                        ));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Cannot place order with empty cart"
            );
        }

        Order order = Order.builder()
                .user(user)
                .address(address)
                .status(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            Inventory inventory = inventoryRepository
                    .findByProductId(product.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Inventory not found for product: "
                                            + product.getName()
                            ));

            int quantity = cartItem.getQuantity();

            if (inventory.getAvailableQuantity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for product: "
                                + product.getName()
                );
            }

            // Reduce inventory
            inventory.setQuantity(
                    inventory.getQuantity() - quantity
            );

            inventoryRepository.save(inventory);

            BigDecimal price = product.getPrice();

            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(quantity)
            );

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);

        // Clear cart after successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        return toResponse(savedOrder);
    }

    // =========================
    // GET USER ORDERS
    // =========================

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(
            Long userId) {

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // GET USER ORDER
    // =========================

    @Transactional(readOnly = true)
    public OrderResponse getUserOrder(
            Long userId,
            Long orderId) {

        Order order = orderRepository
                .findByIdAndUserId(orderId, userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        return toResponse(order);
    }

    // =========================
    // CANCEL ORDER
    // =========================

    @Transactional
    public OrderResponse cancelOrder(
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
                    "Order is already cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "Order cannot be cancelled at this stage"
            );
        }

        // Restore inventory
        for (OrderItem item : order.getItems()) {

            Inventory inventory =
                    inventoryRepository
                            .findByProductId(
                                    item.getProduct().getId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Inventory not found"
                                    ));

            inventory.setQuantity(
                    inventory.getQuantity()
                            + item.getQuantity()
            );

            inventoryRepository.save(inventory);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order updatedOrder =
                orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    // =========================
    // ADMIN: UPDATE STATUS
    // =========================

    @Transactional
    public OrderResponse updateStatus(
            Long orderId,
            OrderStatus status) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        if (!isValidStatusTransition(
                order.getStatus(),
                status)) {

            throw new RuntimeException(
                    "Invalid order status transition from "
                            + order.getStatus()
                            + " to "
                            + status
            );
        }

        order.setStatus(status);

        Order updatedOrder =
                orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    // =========================
    // ADMIN: GET BY STATUS
    // =========================

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(
            OrderStatus status) {

        return orderRepository
                .findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================
    // GET USER
    // =========================

    private User getUser(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    // =========================
    // RESPONSE MAPPER
    // =========================

    private OrderResponse toResponse(
            Order order) {

        Address address = order.getAddress();

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .addressId(address.getId())
                .deliveryName(address.getFullName())
                .deliveryPhone(address.getPhone())
                .deliveryAddress(address.getAddressLine())
                .deliveryCity(address.getCity())
                .deliveryState(address.getState())
                .deliveryPostalCode(address.getPostalCode())
                .deliveryCountry(address.getCountry())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    private OrderItemResponse toItemResponse(
            OrderItem item) {

        Product product = item.getProduct();

        return OrderItemResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));

        return toResponse(order);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByPaymentStatus(
            PaymentStatus paymentStatus) {

        return orderRepository
                .findByPaymentStatusOrderByCreatedAtDesc(
                        paymentStatus
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    private boolean isValidStatusTransition(
            OrderStatus current,
            OrderStatus next) {

        if (current == OrderStatus.CANCELLED) {
            return false;
        }

        if (current == OrderStatus.DELIVERED) {
            return false;
        }

        return switch (current) {

            case PLACED ->
                    next == OrderStatus.CONFIRMED
                            || next == OrderStatus.CANCELLED;

            case CONFIRMED ->
                    next == OrderStatus.PROCESSING
                            || next == OrderStatus.CANCELLED;

            case PROCESSING ->
                    next == OrderStatus.SHIPPED
                            || next == OrderStatus.CANCELLED;

            case SHIPPED ->
                    next == OrderStatus.DELIVERED;

            case DELIVERED,
                 CANCELLED -> false;
        };
    }
}