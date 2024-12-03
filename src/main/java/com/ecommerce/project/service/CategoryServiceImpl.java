package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.MyResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        List<Category> categories = categoryRepository.findAll();

        if(categories.isEmpty()) {
            throw new ApiException("No categories found");
        }
        return categories;
    }

    /**
     * Creates a new category in the system.
     *
     * @param categories the list of categories to be created
     */
    @Override
    public void createCategory(List<Category> categories) {
        for (Category category: categories) {
             Category existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
             if(existingCategory != null) {
                 throw new ApiException("Category with name: " + category.getCategoryName() + " already exists !!!");
             }
        }
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
        Optional<Category> category = categoryRepository.findById(categoryId);

        Category deletedCategory = category.orElseThrow(() ->new MyResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(deletedCategory);

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
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId); // Find the category by ID>
        Category existingCategory = categoryOptional.orElseThrow(() -> new MyResourceNotFoundException("Category","categoryId",categoryId));

        existingCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(existingCategory);
        return "Category with id: " + categoryId + " was updated successfully";
    }
}
