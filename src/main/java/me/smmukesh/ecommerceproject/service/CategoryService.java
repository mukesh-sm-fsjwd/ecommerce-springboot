package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found"));
        Optionalcategory.setCategoryName(updatedCategory.getCategoryName());
        categoryRepository.save(Optionalcategory);
    }

    public boolean deleteCategory(Long categoryId){
        if(categoryRepository.existsById(categoryId)){
            categoryRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }
}
