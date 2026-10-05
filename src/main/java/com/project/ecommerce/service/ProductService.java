package com.project.ecommerce.service;

import com.project.ecommerce.dto.product.ProductRequest;
import com.project.ecommerce.dto.product.ProductResponse;
import com.project.ecommerce.entity.Product;
import com.project.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(
            ProductRequest request) {

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .active(true)
                .build();

        Product savedProduct =
                productRepository.save(product);

        return toResponse(savedProduct);
    }

    public Page<ProductResponse> getProducts(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable =
                PageRequest.of(page, size, sort);

        return productRepository
                .findByActiveTrue(pageable)
                .map(this::toResponse);
    }

    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                ));

        return toResponse(product);
    }

    public Page<ProductResponse> searchProducts(
            String keyword,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("name").ascending()
                );

        return productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        keyword,
                        pageable
                )
                .map(this::toResponse);
    }

    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product =
                productRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                ));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());

        Product updatedProduct =
                productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {

        Product product =
                productRepository
                        .findByIdAndActiveTrue(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"
                                ));

        product.setActive(false);

        productRepository.save(product);
    }

    private ProductResponse toResponse(
            Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .build();
    }
}