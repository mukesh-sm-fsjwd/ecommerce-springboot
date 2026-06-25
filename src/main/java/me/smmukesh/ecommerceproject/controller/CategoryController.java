package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.config.AppConstants;
import me.smmukesh.ecommerceproject.dto.request.CategoryRequest;
import me.smmukesh.ecommerceproject.dto.response.CategoryResponse;
import me.smmukesh.ecommerceproject.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api")
public class CategoryController {

    private CategoryService categorieService;

    public CategoryController(CategoryService categorieService){
        this.categorieService = categorieService;
    }

    @GetMapping("public/categories")
    public ResponseEntity<CategoryResponse> getCategories(
            @RequestParam(name = "pageNumber" , defaultValue = AppConstants.PAGE_NUMBER , required = false) Integer pageNumber,
            @RequestParam(name = "pageSize" , defaultValue = AppConstants.PAGE_SIZE , required = false) Integer pageSize,
            @RequestParam(name = "sortBy" , defaultValue = AppConstants.SORT_CATEGORIES_BY , required = false) String sortBy,
            @RequestParam(name = "sortOrder" , defaultValue = AppConstants.SORT_DIRECTION , required = false) String SortOrder
    ){
        CategoryResponse allCategories = categorieService.getAllCategories(pageNumber,pageSize, sortBy,SortOrder);
        return new ResponseEntity<>(allCategories,HttpStatus.OK);
    }

    @PostMapping("public/category")
    public ResponseEntity<CategoryRequest> addCategory(@Valid @RequestBody CategoryRequest categoryRequest){
        CategoryRequest addedCategory = categorieService.createCategory(categoryRequest);
        return new ResponseEntity<>(addedCategory,HttpStatus.CREATED);
    }

    @PutMapping("public/categories/{categoryId}")
    public ResponseEntity<CategoryRequest> updateCategory(@Valid @RequestBody CategoryRequest categoryRequest,@PathVariable Long categoryId){
        CategoryRequest updatedCategory = categorieService.updateCategory(categoryRequest,categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedCategory);
    }

    @DeleteMapping("admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable long id){
        CategoryRequest deletedCategory = categorieService.deleteCategory(id);
       return ResponseEntity.status(HttpStatus.OK)
               .body("Category "+deletedCategory.getCategoryName()+"was Deleted Successfully");
    }

}
