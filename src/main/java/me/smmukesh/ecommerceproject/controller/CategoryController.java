package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.dto.response.CategoryResponse;
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
    public ResponseEntity<CategoryResponse> getCategories(){
        CategoryResponse allCategories = categorieService.getAllCategories();
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    @PostMapping("public/category")
    public ResponseEntity<String> addCategory(@Valid @RequestBody Category category){
        String addedCategory = categorieService.createCategory(category);
        return new ResponseEntity<>(addedCategory,HttpStatus.CREATED);
    }

    @PutMapping("public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@Valid @RequestBody Category category,@PathVariable Long categoryId){
        String updatedCategory = categorieService.updateCategory(category,categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedCategory);
    }

    @DeleteMapping("admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable long id){
       String deletedCategory = categorieService.deleteCategory(id);
       return ResponseEntity.status(HttpStatus.OK)
               .body(deletedCategory);
    }

}
