package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    List<Category> categories = new ArrayList<>();

    public String addCategory(Category category){
        categories.add(category);
        return "Category Added successfully!";
    }

    public List<Category> getAllCategories(){
        return categories;
    }

}
