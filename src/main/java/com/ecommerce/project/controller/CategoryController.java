package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortOrder
    ) {
        CategoryResponse allCategories = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(allCategories, HttpStatus.OK);
    }
    /**
     * Create a new category. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param category the category to be created
     * @return a success message
     */
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryDTO category) {
        CategoryDTO createdCategory = categoryService.createCategory(category);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
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
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId) {
            CategoryDTO deletedCategoryDTO = categoryService.deleteCategory(categoryId); // Call the deleteCategory method.
            return new ResponseEntity<>(deletedCategoryDTO, HttpStatus.OK);
    }
    /**
     * Updates a category with the specified ID. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param categoryId the ID of the category to be updated
     * @param categoryDTO the category to be updated
     * @return a success message if the category was found and updated,
     *         otherwise a message indicating that the category was not found
     */
    @PutMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @Valid@RequestBody CategoryDTO categoryDTO) {

        CategoryDTO updateCategory = categoryService.updateCategory(categoryId, categoryDTO); // Call the updateCategory method.
        return new ResponseEntity<>(updateCategory, HttpStatus.OK);

    }
}
