package com.project.ecommerce.controller;

import com.project.ecommerce.dto.address.AddressRequest;
import com.project.ecommerce.dto.address.AddressResponse;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(
                        user.getId(),
                        request
                ));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAddresses(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                addressService.getAddresses(user.getId())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                addressService.updateAddress(
                        user.getId(),
                        id,
                        request
                )
        );
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<AddressResponse> setDefaultAddress(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                addressService.setDefaultAddress(
                        user.getId(),
                        id
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        addressService.deleteAddress(
                user.getId(),
                id
        );

        return ResponseEntity.ok(
                "Address deleted successfully"
        );
    }
}