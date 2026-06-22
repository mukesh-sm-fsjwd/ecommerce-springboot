package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class CategoryController {

    private CategoryService categorieService;

    public CategoryController(CategoryService categorieService){
        this.categorieService = categorieService;
    }

    @GetMapping("public/categories")
    public List<Category> getCategories(){
        return categorieService.getAllCategories();
    }

    @PostMapping("public/category")
    public String addCategory(@RequestBody Category category){
        return categorieService.createCategory(category);
    }

}
