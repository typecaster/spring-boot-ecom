package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Getter
@Setter
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    /**
     * Retrieve the list of all categories.
     *
     * @return a list of categories available
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Creates a new category in the system.
     *
     * @param categories the list of categories to be created
     */
    @Override
    public void createCategory(List<Category> categories) {
        categoryRepository.saveAll(categories);
    }

    /**
     * Deletes a category with the specified ID.
     *
     * @param categoryId the ID of the category to be deleted
     * @return a success message if the category was found and deleted,
     *         otherwise a message indicating that the category was not found
     */
    @Override
    public String deleteCategory(Long categoryId) {
        List<Category> categories = getAllCategories(); // Replace with your actual list of categories>
        Category category = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + categoryId));

        categoryRepository.delete(category);
        return "Category with id: " + categoryId + " was deleted successfully";
    }

    /**
     * Updates a category with the specified ID.
     *
     * @param categoryId the ID of the category to be updated
     * @param category the category to be updated
     * @return a success message if the category was found and updated,
     *         otherwise a message indicating that the category was not found
     */
    @Override
    public String updateCategory(Long categoryId, Category category) {
        List<Category> categories = getAllCategories();
        Category existingCategory = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + categoryId));

        existingCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(existingCategory);
        return "Category with id: " + categoryId + " was updated successfully";
    }
}
