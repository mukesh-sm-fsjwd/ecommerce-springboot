package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.response.APIResponse;
import me.smmukesh.ecommerceproject.dto.response.ProductSemanticSearchResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ProductEmbeddingService {

    private final VectorStore vectorStore;
    private final ProductRepository productRepository;
    private final AtomicBoolean isIndexed = new AtomicBoolean(false);

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Autowired
    public ProductEmbeddingService(VectorStore vectorStore, ProductRepository productRepository) {
        this.vectorStore = vectorStore;
        this.productRepository = productRepository;
    }

    public APIResponse syncProductsToVectorStore() {
        validateApiKey();

        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return new APIResponse("No products found in the database to index.", false);
        }

        List<Document> documents = new ArrayList<>();
        for (Product product : products) {
            documents.add(createDocumentFromProduct(product));
        }

        try {
            vectorStore.add(documents);
            isIndexed.set(true);
            return new APIResponse("Successfully indexed " + documents.size() + " products into the vector store.", true);
        } catch (Exception e) {
            throw new APIException("Failed to index products into vector store: " + e.getMessage());
        }
    }

    public List<ProductSemanticSearchResponse> searchProductsSemantically(String query, int topK, double similarityThreshold) {
        validateApiKey();

        if (query == null || query.trim().isEmpty()) {
            throw new APIException("Search query cannot be empty.");
        }

        // Auto-index on first search if not already synced
        if (!isIndexed.get()) {
            syncProductsToVectorStore();
        }

        try {
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(query)
                    .topK(topK > 0 ? topK : 5)
                    .similarityThreshold(similarityThreshold >= 0.0 ? similarityThreshold : 0.0)
                    .build();

            List<Document> matchedDocuments = vectorStore.similaritySearch(searchRequest);
            List<ProductSemanticSearchResponse> results = new ArrayList<>();

            for (Document doc : matchedDocuments) {
                Map<String, Object> metadata = doc.getMetadata();
                ProductSemanticSearchResponse response = new ProductSemanticSearchResponse();

                Object productIdObj = metadata.get("productId");
                if (productIdObj instanceof Number number) {
                    response.setProductId(number.longValue());
                } else if (productIdObj != null) {
                    response.setProductId(Long.parseLong(productIdObj.toString()));
                }

                response.setProductName((String) metadata.get("productName"));
                response.setCategoryName((String) metadata.get("categoryName"));
                response.setDescription((String) metadata.get("description"));

                Object priceObj = metadata.get("price");
                if (priceObj instanceof Number number) {
                    response.setPrice(number.doubleValue());
                }

                Object discountObj = metadata.get("discount");
                if (discountObj instanceof Number number) {
                    response.setDiscount(number.doubleValue());
                }

                Object specialPriceObj = metadata.get("specialPrice");
                if (specialPriceObj instanceof Number number) {
                    response.setSpecialPrice(number.doubleValue());
                }

                response.setImage((String) metadata.get("image"));
                response.setRelevanceScore(doc.getScore());

                results.add(response);
            }

            return results;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("Semantic search failed: " + e.getMessage());
        }
    }

    public void updateProductInVectorStore(Product product) {
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.trim().equals("${OPENAI_API_KEY:}")) {
            try {
                Document document = createDocumentFromProduct(product);
                vectorStore.add(List.of(document));
            } catch (Exception ignored) {
                // Keep resilient
            }
        }
    }

    private Document createDocumentFromProduct(Product product) {
        String categoryName = product.getCategory() != null ? product.getCategory().getCategoryName() : "General";
        String description = product.getDescription() != null ? product.getDescription() : "";
        Double price = product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice();

        String content = String.format("Product: %s. Category: %s. Description: %s. Price: %.2f.",
                product.getProductName(), categoryName, description, price != null ? price : 0.0);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("productId", product.getProductId());
        metadata.put("productName", product.getProductName());
        metadata.put("categoryName", categoryName);
        metadata.put("description", description);
        metadata.put("price", product.getPrice());
        metadata.put("discount", product.getDiscount());
        metadata.put("specialPrice", product.getSpecialPrice());
        metadata.put("image", product.getImage() != null ? product.getImage() : "");

        return new Document(String.valueOf(product.getProductId()), content, metadata);
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.trim().equals("${OPENAI_API_KEY:}")) {
            throw new APIException("OpenAI API key is missing. Please set the OPENAI_API_KEY environment variable to use AI features.");
        }
    }
}
