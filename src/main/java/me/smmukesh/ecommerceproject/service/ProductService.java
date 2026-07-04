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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

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

    public ProductRequest addProduct(ProductRequest productRequest,Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));
        Product product = modelMapper.map(productRequest,Product.class);
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

    public ProductRequest updateProduct(ProductRequest productRequest,long productId) {
        //1. Get the existing product from db
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));

        //2. update the product
        Product product = modelMapper.map(productRequest,Product.class);
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

    public ProductRequest deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));
        productRepository.deleteById(productId);
        return modelMapper.map(product,ProductRequest.class);
    }

    public ProductRequest updateProductImage(long productId, MultipartFile image) throws IOException {
        // 1. Get the product from db.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));
        // 2. Upload the image to the server.
        // 3. Get the file name from the image.
        String path = "images/";
        String fileName = uploadImage(path,image);

        // 4. Update the file name to the product.
        product.setImage(fileName);

        // 5. Save the product
        Product savedProduct = productRepository.save(product);

        // 6. Return the ProductRequest.
        return modelMapper.map(savedProduct,ProductRequest.class);
    }

    private String uploadImage(String path, MultipartFile file) throws IOException {

        // 1. File name of the original file;
        String originalFileName = file.getOriginalFilename();

        // 2. Generate a unique file name (to avoid name conflict)
        String fileName = createUniqueId(originalFileName);
        String filePath = path + File.separator + fileName;

        // 3. Check if the path exists and creates.
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.mkdir();
        }

        // 4. upload the file to the server.
        Files.copy(file.getInputStream(), Path.of(filePath));

        // 5. Return the file name.
        return fileName;
    }

    private String createUniqueId(String originalFileName) {
        String id = UUID.randomUUID().toString();
        return id.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
    }


}
