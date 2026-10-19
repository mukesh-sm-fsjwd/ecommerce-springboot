package me.smmukesh.ecommerceproject.controller;

import jakarta.validation.Valid;
import me.smmukesh.ecommerceproject.dto.request.ProductDescriptionGenerateRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductDescriptionResponse;
import me.smmukesh.ecommerceproject.service.ProductAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductAiController {

    private final ProductAiService productAiService;

    @Autowired
    public ProductAiController(ProductAiService productAiService) {
        this.productAiService = productAiService;
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
}
