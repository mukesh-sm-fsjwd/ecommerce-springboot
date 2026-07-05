package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.dto.request.ProductRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductResponse;
import me.smmukesh.ecommerceproject.service.CategoryService;
import me.smmukesh.ecommerceproject.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public ResponseEntity<ProductRequest> addProduct(@Valid @RequestBody ProductRequest productRequest, @PathVariable Long categoryId){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(productRequest,categoryId));
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

    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword){
        ProductResponse productResponse = productService.searchByKeyword(keyword);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productResponse);
    }

    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductRequest> updateProduct(@Valid @RequestBody ProductRequest productRequest,@PathVariable long productId){
        ProductRequest savedProductRequest = productService.updateProduct(productRequest,productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(savedProductRequest);
    }

    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<String> deleteProductById(@PathVariable long productId){
       ProductRequest product = productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Product '"+product.getProductName()+"' was Deleted Successfully");
    }

    @PutMapping("admin/products/{productId}/image")
    public ResponseEntity<ProductRequest> updateProductImage(
            @PathVariable long productId,
            @RequestParam("image") MultipartFile image) throws IOException {
        ProductRequest productRequest = productService.updateProductImage(productId,image);
        return ResponseEntity.status(HttpStatus.OK)
                .body(productRequest);
    }
}
