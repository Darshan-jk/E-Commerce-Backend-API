package com.project.ecommerce.service;

import com.project.ecommerce.entity.Category;
import com.project.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(Category category) {

        if (category.getName() == null ||
                category.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Category name is required"
            );
        }

        if (categoryRepository.existsByNameIgnoreCase(
                category.getName().trim())) {

            throw new RuntimeException(
                    "Category already exists"
            );
        }

        category.setName(category.getName().trim());

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {

        return categoryRepository.findByActiveTrue();
    }

    public Category getCategoryById(Long id) {

        return categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Category not found"
                        ));
    }

    public Category updateCategory(
            Long id,
            Category updatedCategory) {

        Category category = getCategoryById(id);

        if (updatedCategory.getName() == null ||
                updatedCategory.getName().trim().isEmpty()) {

            throw new RuntimeException(
                    "Category name is required"
            );
        }

        String newName =
                updatedCategory.getName().trim();

        if (!newName.equalsIgnoreCase(category.getName())
                && categoryRepository
                .existsByNameIgnoreCase(newName)) {

            throw new RuntimeException(
                    "Category already exists"
            );
        }

        category.setName(newName);
        category.setDescription(
                updatedCategory.getDescription()
        );

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {

        Category category = getCategoryById(id);

        // Soft delete
        category.setActive(false);

        categoryRepository.save(category);
    }
}