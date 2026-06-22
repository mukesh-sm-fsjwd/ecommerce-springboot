package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    public CategoryService(CategoryRepository categoryRepository){
//        this.categoryRepository = categoryRepository;
//    }

    List<Category> categories = new ArrayList<>();
    private Long Id = 1L;

    public String createCategory(Category category){
        category.setCategoryId(Id++);
        categories.add(category);
        return "Category Added successfully!";
    }

    public List<Category> getAllCategories(){
        return categories;
    }

}
