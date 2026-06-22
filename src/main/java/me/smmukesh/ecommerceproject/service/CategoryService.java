package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public boolean deleteCategory(int categoryId){
        boolean isDone = false;
        Optional<Category> category = categories.stream()
                .filter(c -> c.getCategoryId() ==  categoryId)
                .findFirst();
        if(category.isPresent()){
            isDone = true;
            categories.remove(category.get());
        }
        return isDone;
    }
}
