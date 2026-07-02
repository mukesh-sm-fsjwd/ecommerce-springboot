package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.ProductRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductResponse;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private ModelMapper modelMapper;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public ProductResponse getAllProducts(){
        List<Product> allProducts = productRepository.findAll();
        List<ProductRequest> productRequests = allProducts.stream()
                .map(product -> modelMapper.map(product,ProductRequest.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productRequests);
        return productResponse;
    }

    public ProductRequest addProduct(Product product,Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct,ProductRequest.class);
    }

    public ProductResponse searchByCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductRequest> productRequests = products.stream()
                .map(product -> modelMapper.map(product,ProductRequest.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productRequests);
        return productResponse;
    }

    public ProductResponse searchByKeyword(String keyword) {
        List<Product> products = productRepository.getProductByProductNameContainingIgnoreCase(keyword);
        List<ProductRequest> productRequests = products.stream()
                .map(product -> modelMapper.map(product, ProductRequest.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productRequests);
        return productResponse;
    }

    public ProductRequest updateProduct(Product product,long productId) {
        //1. Get the existing product from db
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));

        //2. update the product
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

        //3. save to db
        productRepository.save(productFromDb);

        return modelMapper.map(productFromDb,ProductRequest.class);
    }
}
