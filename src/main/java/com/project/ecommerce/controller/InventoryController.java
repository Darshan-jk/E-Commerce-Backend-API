package com.project.ecommerce.controller;

import com.project.ecommerce.dto.inventory.InventoryRequest;
import com.project.ecommerce.dto.inventory.InventoryResponse;
import com.project.ecommerce.dto.inventory.StockRequest;
import com.project.ecommerce.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody InventoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        inventoryService.createInventory(request)
                );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                inventoryService.getInventoryByProduct(
                        productId
                )
        );
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        productId,
                        request
                )
        );
    }

    @PatchMapping("/product/{productId}/add")
    public ResponseEntity<InventoryResponse> addStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockRequest request) {

        return ResponseEntity.ok(
                inventoryService.addStock(
                        productId,
                        request
                )
        );
    }

    @PatchMapping("/product/{productId}/reduce")
    public ResponseEntity<InventoryResponse> reduceStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockRequest request) {

        return ResponseEntity.ok(
                inventoryService.reduceStock(
                        productId,
                        request
                )
        );
    }
}