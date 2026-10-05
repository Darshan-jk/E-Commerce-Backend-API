package com.project.ecommerce.service;

import com.project.ecommerce.dto.inventory.InventoryRequest;
import com.project.ecommerce.dto.inventory.InventoryResponse;
import com.project.ecommerce.dto.inventory.StockRequest;
import com.project.ecommerce.entity.Inventory;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.repository.InventoryRepository;
import com.project.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public InventoryResponse createInventory(
            InventoryRequest request) {

        if (inventoryRepository.existsByProductId(
                request.getProductId())) {

            throw new RuntimeException(
                    "Inventory already exists for this product"
            );
        }

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found"
                        ));

        Inventory inventory = Inventory.builder()
                .product(product)
                .quantity(request.getQuantity())
                .reservedQuantity(0)
                .build();

        Inventory savedInventory =
                inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProduct(
            Long productId) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found"
                                ));

        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse updateInventory(
            Long productId,
            InventoryRequest request) {

        Inventory inventory =
                inventoryRepository.findByProductId(productId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Inventory not found"
                                ));

        inventory.setQuantity(request.getQuantity());

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return toResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse addStock(
            Long productId,
            StockRequest request) {

        Inventory inventory =
                getInventory(productId);

        inventory.setQuantity(
                inventory.getQuantity()
                        + request.getQuantity()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return toResponse(updatedInventory);
    }

    @Transactional
    public InventoryResponse reduceStock(
            Long productId,
            StockRequest request) {

        Inventory inventory =
                getInventory(productId);

        int available =
                inventory.getAvailableQuantity();

        if (available < request.getQuantity()) {
            throw new RuntimeException(
                    "Insufficient available stock"
            );
        }

        inventory.setQuantity(
                inventory.getQuantity()
                        - request.getQuantity()
        );

        Inventory updatedInventory =
                inventoryRepository.save(inventory);

        return toResponse(updatedInventory);
    }

    private Inventory getInventory(Long productId) {

        return inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Inventory not found"
                        ));
    }

    private InventoryResponse toResponse(
            Inventory inventory) {

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .reservedQuantity(
                        inventory.getReservedQuantity()
                )
                .availableQuantity(
                        inventory.getAvailableQuantity()
                )
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}