package com.project.ecommerce.controller;

import com.project.ecommerce.dto.cart.CartResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // =========================
    // GET CART
    // =========================

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.getCart(user.getId())
        );
    }

    // =========================
    // ADD PRODUCT
    // =========================

    @PostMapping("/items/{productId}")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.addToCart(
                        user.getId(),
                        productId,
                        quantity
                )
        );
    }

    // =========================
    // UPDATE PRODUCT
    // =========================

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.updateCartItem(
                        user.getId(),
                        productId,
                        quantity
                )
        );
    }

    // =========================
    // REMOVE PRODUCT
    // =========================

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(
            @PathVariable Long productId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.removeFromCart(
                        user.getId(),
                        productId
                )
        );
    }

    // =========================
    // CLEAR CART
    // =========================

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        cartService.clearCart(user.getId());

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}