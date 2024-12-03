package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.MyResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;
    /**
     * Retrieve the list of all categories.
     *
     * @return a list of categories available
     */
    @Override
    public CategoryResponse getAllCategories() {

        List<Category> categories = categoryRepository.findAll();

        if(categories.isEmpty()) {
            throw new ApiException("No categories found");
        }
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);

        return categoryResponse;
    }


    /**
     * Create a new category. This endpoint is only accessible to users with the
     * {@code ROLE_ADMIN} role.
     *
     * @param categoryDTO the category to be created
     * @return the created category
     * @throws ApiException if a category with the same name already exists
     */
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
         Category category = modelMapper.map(categoryDTO, Category.class);
         Category existingCategory = categoryRepository.findByCategoryName(category.getCategoryName());
         if(existingCategory != null) {
             throw new ApiException("Category with name: " + category.getCategoryName() + " already exists !!!");
         }
         Category savedCategory = categoryRepository.save(category);
         return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /**
     * Deletes a category with the specified ID.
     *
     * @param categoryId the ID of the category to be deleted
     * @return a success message if the category was found and deleted,
     *         otherwise a message indicating that the category was not found
     */
    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Optional<Category> category = categoryRepository.findById(categoryId);

        Category deletedCategory = category.orElseThrow(() ->new MyResourceNotFoundException("Category","categoryId",categoryId));
        categoryRepository.delete(deletedCategory);

        return modelMapper.map(deletedCategory, CategoryDTO.class);
    }

    /**
     * Updates a category with the specified ID.
     *
     * @param categoryId the ID of the category to be updated
     * @param categoryDTO the category to be updated
     * @return a success message if the category was found and updated,
     *         otherwise a message indicating that the category was not found
     */
    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId); // Find the category by ID>
        Category existingCategory = categoryOptional.orElseThrow(() -> new MyResourceNotFoundException("Category","categoryId",categoryId));

        existingCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(existingCategory);
        return modelMapper.map(existingCategory, CategoryDTO.class);
    }
}
