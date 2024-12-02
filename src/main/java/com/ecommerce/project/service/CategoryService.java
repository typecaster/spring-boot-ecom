package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    void createCategory(List<Category> categories);

    String deleteCategory(Long categoryId);

    String updateCategory(Long categoryId, Category category);
}
