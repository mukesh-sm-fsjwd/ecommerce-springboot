package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.dto.request.ProductDescriptionGenerateRequest;
import me.smmukesh.ecommerceproject.dto.response.APIResponse;
import me.smmukesh.ecommerceproject.dto.response.ProductDescriptionResponse;
import me.smmukesh.ecommerceproject.dto.response.ProductImageAnalysisResponse;
import me.smmukesh.ecommerceproject.dto.response.ProductSemanticSearchResponse;
import me.smmukesh.ecommerceproject.service.ProductAiService;
import me.smmukesh.ecommerceproject.service.ProductEmbeddingService;
import me.smmukesh.ecommerceproject.service.ProductImageAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductAiController {

    private final ProductAiService productAiService;
    private final ProductEmbeddingService productEmbeddingService;
    private final ProductImageAiService productImageAiService;

    @Autowired
    public ProductAiController(ProductAiService productAiService,
                               ProductEmbeddingService productEmbeddingService,
                               ProductImageAiService productImageAiService) {
        this.productAiService = productAiService;
        this.productEmbeddingService = productEmbeddingService;
        this.productImageAiService = productImageAiService;
    }

    @PostMapping("/admin/products/generate-description")
    public ResponseEntity<ProductDescriptionResponse> generateDescription(
            @Valid @RequestBody ProductDescriptionGenerateRequest request) {
        ProductDescriptionResponse response = productAiService.generateDescription(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/admin/products/{productId}/generate-description", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ProductDescriptionResponse> generateDescriptionForExistingProduct(
            @PathVariable Long productId,
            @RequestParam(value = "tone", required = false, defaultValue = "Professional and engaging") String tone,
            @RequestParam(value = "targetAudience", required = false, defaultValue = "General consumers") String targetAudience) {
        ProductDescriptionResponse response = productAiService.generateDescriptionForExistingProduct(productId, tone, targetAudience);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/public/products/search/semantic")
    public ResponseEntity<List<ProductSemanticSearchResponse>> searchProductsSemantically(
            @RequestParam("query") String query,
            @RequestParam(value = "topK", defaultValue = "5", required = false) int topK,
            @RequestParam(value = "similarityThreshold", defaultValue = "0.0", required = false) double similarityThreshold) {
        List<ProductSemanticSearchResponse> results = productEmbeddingService.searchProductsSemantically(query, topK, similarityThreshold);
        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

    @RequestMapping(value = "/admin/products/sync-embeddings", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<APIResponse> syncProductEmbeddings() {
        APIResponse response = productEmbeddingService.syncProductsToVectorStore();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/admin/products/analyze-image")
    public ResponseEntity<ProductImageAnalysisResponse> analyzeProductImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "hint", required = false) String hint) {
        ProductImageAnalysisResponse response = productImageAiService.analyzeImage(image, hint);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/admin/products/{productId}/analyze-image", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ProductImageAnalysisResponse> analyzeExistingProductImage(
            @PathVariable Long productId) {
        ProductImageAnalysisResponse response = productImageAiService.analyzeExistingProductImage(productId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
