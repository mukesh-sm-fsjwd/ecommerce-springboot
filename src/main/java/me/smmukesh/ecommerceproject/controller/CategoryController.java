package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Category>> getCategories(){
        List<Category> allCategories = categorieService.getAllCategories();
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    @PostMapping("public/category")
    public ResponseEntity<String> addCategory(@RequestBody Category category){
        String addedCategory = categorieService.createCategory(category);
        return new ResponseEntity<>(addedCategory,HttpStatus.CREATED);
    }

    @DeleteMapping("admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id){
       boolean isDeleted = categorieService.deleteCategory(id);
       if(isDeleted){
           return ResponseEntity.status(HttpStatus.OK)
                   .body("Category Deletion Successful!");
       }else{
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("Category Not Found!");
       }
    }

}
