package com.project.ecommerce.service;

import com.project.ecommerce.dto.cart.CartItemResponse;
import com.project.ecommerce.dto.cart.CartResponse;
import com.project.ecommerce.entity.Cart;
import com.project.ecommerce.entity.CartItem;
import com.project.ecommerce.entity.Inventory;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.CartItemRepository;
import com.project.ecommerce.repository.CartRepository;
import com.project.ecommerce.repository.InventoryRepository;
import com.project.ecommerce.repository.ProductRepository;
import com.project.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    // =========================
    // GET CART
    // =========================

    @Transactional
    public CartResponse getCart(Long userId) {

        Cart cart = getOrCreateCart(userId);

        return toResponse(cart);
    }

    // =========================
    // ADD PRODUCT TO CART
    // =========================

    @Transactional
    public CartResponse addToCart(
            Long userId,
            Long productId,
            Integer quantity) {

        validateQuantity(quantity);

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository
                .findByIdAndActiveTrue(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found for this product"
                        ));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient product stock"
            );
        }

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        productId
                )
                .orElse(null);

        if (cartItem == null) {

            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();

        } else {

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            if (inventory.getAvailableQuantity() < newQuantity) {
                throw new RuntimeException(
                        "Insufficient product stock"
                );
            }

            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);

        return toResponse(cart);
    }

    // =========================
    // UPDATE CART ITEM
    // =========================

    @Transactional
    public CartResponse updateCartItem(
            Long userId,
            Long productId,
            Integer quantity) {

        validateQuantity(quantity);

        Cart cart = getCartEntity(userId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        productId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found in cart"
                        ));

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found for this product"
                        ));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new RuntimeException(
                    "Insufficient product stock"
            );
        }

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return toResponse(cart);
    }

    // =========================
    // REMOVE PRODUCT
    // =========================

    @Transactional
    public CartResponse removeFromCart(
            Long userId,
            Long productId) {

        Cart cart = getCartEntity(userId);

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(
                        cart.getId(),
                        productId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found in cart"
                        ));

        cartItemRepository.delete(cartItem);

        return toResponse(cart);
    }

    // =========================
    // CLEAR CART
    // =========================

    @Transactional
    public void clearCart(Long userId) {

        Cart cart = getCartEntity(userId);

        cart.getItems().clear();

        cartRepository.save(cart);
    }

    // =========================
    // GET OR CREATE CART
    // =========================

    private Cart getOrCreateCart(Long userId) {

        return cartRepository
                .findByUserId(userId)
                .orElseGet(() -> {

                    User user = userRepository
                            .findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    ));

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(cart);
                });
    }

    // =========================
    // GET EXISTING CART
    // =========================

    private Cart getCartEntity(Long userId) {

        return cartRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cart not found"
                        ));
    }

    // =========================
    // VALIDATE QUANTITY
    // =========================

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }
    }

    // =========================
    // CONVERT TO RESPONSE
    // =========================

    private CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .total(total)
                .build();
    }

    // =========================
    // CART ITEM RESPONSE
    // =========================

    private CartItemResponse toItemResponse(
            CartItem item) {

        Product product = item.getProduct();

        BigDecimal subtotal =
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()
                                )
                        );

        return CartItemResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}