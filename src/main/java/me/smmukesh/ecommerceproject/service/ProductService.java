package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.CartDTO;
import me.smmukesh.ecommerceproject.dto.request.ProductRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Cart;
import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.CartRepository;
import me.smmukesh.ecommerceproject.repository.CategoryRepository;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final CartService cartService;

    @Value("${project.image}")
    String path = "images/";

    public ProductService(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            FileService fileService,
            ModelMapper modelMapper,
                          CartRepository cartRepository,
                          CartService cartService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
    }

    public ProductResponse getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> allProducts = pageProducts.getContent();
        if (allProducts.isEmpty()) {
            throw new APIException("No Products Added.");
        }
        List<ProductRequest> productRequests = allProducts.stream()
                .map(product -> modelMapper.map(product, ProductRequest.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productRequests);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    public ProductRequest addProduct(ProductRequest productRequest, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product product : products) {
            if (product.getProductName().equals(productRequest.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {
            Product product = modelMapper.map(productRequest, Product.class);
            product.setCategory(category);
            product.setImage("default.png");
            double specialPrice = product.getPrice() - product.getDiscount();
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductRequest.class);
        } else {
            throw new APIException(
                    "Product with the name : " + productRequest.getProductName() + " is already present.");
        }
    }

    public ProductResponse searchByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy,
            String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);

        List<Product> products = pageProducts.getContent();

        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + " Category has no Products.");
        }

        List<ProductRequest> productRequests = products.stream()
                .map(product -> modelMapper.map(product, ProductRequest.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productRequests);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    public ProductResponse searchByKeyword(String keyword, int pageNumber, int pageSize, String sortBy,
            String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.getProductByProductNameContainingIgnoreCase(keyword,
                pageDetails);

        List<Product> products = pageProducts.getContent();
        List<ProductRequest> productRequests = products.stream()
                .map(product -> modelMapper.map(product, ProductRequest.class))
                .toList();

        if (products.isEmpty()) {
            throw new APIException("No Products Found with keyword : " + keyword);
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productRequests);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    public ProductRequest updateProduct(ProductRequest productRequest, long productId) {
        // 1. Get the existing product from db
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        // 2. update the product
        Product product = modelMapper.map(productRequest, Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        double updatedSpecialPrice = productFromDb.getPrice() - productFromDb.getDiscount();
        productFromDb.setSpecialPrice(updatedSpecialPrice);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
                    List<ProductRequest> productRequests = cart.getCartItems().stream()
                            .map(p -> modelMapper.map(p.getProduct(),ProductRequest.class))
                            .toList();
                    cartDTO.setProducts(productRequests);
                    return cartDTO;
                }).toList();

        cartDTOs.forEach(cart -> cartService.updateProductsInCart(cart.getCartId(),productId));
        // 3. save to db
        productRepository.save(productFromDb);

        return modelMapper.map(productFromDb, ProductRequest.class);
    }

    public ProductRequest deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(),productId));
        productRepository.deleteById(productId);
        return modelMapper.map(product, ProductRequest.class);
    }

    public ProductRequest updateProductImage(long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        if (product.getImages().size() >= 12) {
            throw new APIException("Product cannot have more than 12 images.");
        }

        String fileName = fileService.uploadImage(path, image);
        if (!product.getImages().contains(fileName)) {
            product.getImages().add(fileName);
        }
        product.setImage(product.getImages().get(0));

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductRequest.class);
    }

    public ProductRequest updateProductImages(long productId, MultipartFile[] imageFiles) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        int currentCount = product.getImages().size();
        int newCount = imageFiles != null ? imageFiles.length : 0;
        if (currentCount + newCount > 15) {
            throw new APIException("A product can have a maximum of 15 images. Current: " + currentCount + ", Attempted: " + newCount);
        }

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (file != null && !file.isEmpty()) {
                    String fileName = fileService.uploadImage(path, file);
                    if (!product.getImages().contains(fileName)) {
                        product.getImages().add(fileName);
                    }
                }
            }
        }

        if (!product.getImages().isEmpty()) {
            product.setImage(product.getImages().get(0));
        }

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductRequest.class);
    }

    public ProductRequest deleteProductImage(long productId, String imageName) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        product.getImages().remove(imageName);
        if (product.getImage() != null && product.getImage().equals(imageName)) {
            product.setImage(product.getImages().isEmpty() ? "default.png" : product.getImages().get(0));
        }

        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductRequest.class);
    }
}
