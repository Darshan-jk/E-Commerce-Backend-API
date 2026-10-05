package com.project.ecommerce.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Product name must be between 2 and 150 characters"
    )
    private String name;

    @NotBlank(message = "Product description is required")
    @Size(
            max = 2000,
            message = "Description cannot exceed 2000 characters"
    )
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    private BigDecimal price;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}