package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }


    public String createCategory(Category category){
        categoryRepository.save(category);
        return "Category Added successfully!";
    }

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public void updateCategory(Category updatedCategory,Long categoryId){
        Category Optionalcategory = categoryRepository.findById(categoryId).orElseThrow(()
                -> new ResourceNotFoundException("Category", "Catgory Id", categoryId));
        Optionalcategory.setCategoryName(updatedCategory.getCategoryName());
        categoryRepository.save(Optionalcategory);
    }

    public String deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Catgory Id", categoryId));
        categoryRepository.save(category);
        return "Category Updated Successfully!";
    }
}
