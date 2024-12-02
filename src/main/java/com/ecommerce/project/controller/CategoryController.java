package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieve the list of all categories.
     *
     * @return a list of categories available
     */
    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getCategories() {
        List<Category> allCategories = categoryService.getAllCategories();
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }
    /**
     * Create a new category. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param categories the category to be created
     * @return a success message
     */
    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@RequestBody List<Category> categories) {
        categoryService.createCategory(categories);
        return new ResponseEntity<>("Category created successfully", HttpStatus.CREATED);
    }

    /**
     * Delete a category with the specified ID. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param categoryId the ID of the category to be deleted
     * @return a success message if the category was found and deleted,
     *         otherwise a message indicating that the category was not found
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        try {
            String status = categoryService.deleteCategory(categoryId); // Call the deleteCategory method.
            return new ResponseEntity<>(status, HttpStatus.OK);
//           return ResponseEntity.ok(status);
//           return ResponseEntity.status(HttpStatus.OK).body(status);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
    /**
     * Updates a category with the specified ID. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param categoryId the ID of the category to be updated
     * @param category the category to be updated
     * @return a success message if the category was found and updated,
     *         otherwise a message indicating that the category was not found
     */
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        try {
            String status = categoryService.updateCategory(categoryId, category); // Call the updateCategory method.
            return new ResponseEntity<>(status, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(), e.getStatusCode());
        }
    }
}
