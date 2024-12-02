package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
public class CategoryServiceImpl implements CategoryService {
    private final List<Category> categories = new ArrayList<>();

    private Long nextId = 1L;
    /**
     * Retrieve the list of all categories.
     *
     * @return a list of categories available
     */
    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    /**
     * Creates a new category in the system.
     *
     * @param categories the list of categories to be created
     */
    @Override
    public void createCategory(List<Category> categories) {
        for (Category category : categories) {
            category.setCategoryId( nextId++ );
            this.categories.add(category);
        }
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
        Category category = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst().orElse(null);

        if (category != null) {
            categories.remove(category);
            return "Category with id: " + categoryId + " was deleted successfully";
        }
        return "Category not found with id: " + categoryId;
    }
}
