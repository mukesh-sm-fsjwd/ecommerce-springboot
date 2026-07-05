package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.ProductRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Category;
import me.smmukesh.ecommerceproject.model.Product;
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

    @Value("${project.image}")
    String path = "images/";

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          FileService fileService,
                          ModelMapper modelMapper){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.fileService = fileService;
        this.modelMapper = modelMapper;
    }

    public ProductResponse getAllProducts(int pageNumber,int pageSize, String sortBy,String sortOrder){

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> allProducts = pageProducts.getContent();
        if(allProducts.isEmpty()){
            throw new APIException("No Products Added.");
        }
        List<ProductRequest> productRequests = allProducts.stream()
                .map(product -> modelMapper.map(product,ProductRequest.class))
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

    public ProductRequest addProduct(ProductRequest productRequest,Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for(Product product : products){
            if(product.getProductName().equals(productRequest.getProductName())){
                isProductNotPresent = false;
                break;
            }
        }

        if(isProductNotPresent){
            Product product = modelMapper.map(productRequest,Product.class);
            product.setCategory(category);
            product.setImage("default.png");
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct,ProductRequest.class);
        }else{
            throw  new APIException("Product with the name : "+productRequest.getProductName()+" is already present.");
        }
    }

    public ProductResponse searchByCategory(Long categoryId, int pageNumber, int pageSize, String sortBy, String sortOrder){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","CategoryId",categoryId));

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> products = pageProducts.getContent();

        if(products.isEmpty()){
            throw new APIException(category.getCategoryName()+" Category has no Products.");
        }

        List<ProductRequest> productRequests = products.stream()
                .map(product -> modelMapper.map(product,ProductRequest.class))
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

    public ProductResponse searchByKeyword(String keyword, int pageNumber, int pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts = productRepository.getProductByProductNameContainingIgnoreCase(keyword, pageDetails);

        List<Product> products = pageProducts.getContent();
        List<ProductRequest> productRequests = products.stream()
                .map(product -> modelMapper.map(product, ProductRequest.class))
                .toList();

        if(products.isEmpty()){
            throw new APIException("No Products Found with keyword : "+keyword);
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

        String fileName = fileService.uploadImage(path,image);

        // 4. Update the file name to the product.
        product.setImage(fileName);

        // 5. Save the product
        Product savedProduct = productRepository.save(product);

        // 6. Return the ProductRequest.
        return modelMapper.map(savedProduct,ProductRequest.class);
    }
}
