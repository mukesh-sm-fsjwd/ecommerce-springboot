package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.dto.request.ProductRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductResponse;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.service.CategoryService;
import me.smmukesh.ecommerceproject.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService){
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @PostMapping("admin/categories/{categoryId}/product")
    public ResponseEntity<ProductRequest> addProduct(@RequestBody Product product,@PathVariable Long categoryId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(product,categoryId));
    }

    @GetMapping("public/products")
    public ResponseEntity<ProductResponse> getAllProducts(){
        ProductResponse productResponse = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK)
                .body(productResponse);
    }

    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId){
        ProductResponse productResponse = productService.searchByCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productResponse);
    }
}
