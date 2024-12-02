package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {
    private final List<Category> categories = new ArrayList<>();
    /**
     * Retrieve the list of all categories.
     *
     * @return a list of categories available
     */
    @GetMapping("/api/public/categories")
    public List<Category> getCategories() {
        return categories;
    }
    /**
     * Create a new category. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param category the category to be created
     * @return a success message
     */
    @PostMapping("/api/public/categories")
    public String createCategory(@RequestBody Category category) {
        categories.add(category);
        return "Category created successfully";
    }
}
