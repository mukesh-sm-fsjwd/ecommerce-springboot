package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.ProductDescriptionGenerateRequest;
import me.smmukesh.ecommerceproject.dto.response.ProductDescriptionResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductAiService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Autowired
    public ProductAiService(ChatClient chatClient, ProductRepository productRepository) {
        this.chatClient = chatClient;
        this.productRepository = productRepository;
    }

    public ProductDescriptionResponse generateDescription(ProductDescriptionGenerateRequest request) {
        validateApiKey();

        try {
            BeanOutputConverter<ProductDescriptionResponse> converter = new BeanOutputConverter<>(
                    ProductDescriptionResponse.class);
            String format = converter.getFormat();

            String template = """
                    You are an expert e-commerce copywriter and SEO marketing specialist.
                    Generate a compelling, high-converting product description and metadata for the following item:

                    - Product Name: {productName}
                    - Category: {categoryName}
                    - Key Highlights / Features: {highlights}
                    - Target Audience: {targetAudience}
                    - Tone: {tone}

                    Ensure your response strictly complies with the following JSON schema:
                    {format}
                    """;

            PromptTemplate promptTemplate = new PromptTemplate(template);

            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("productName", request.getProductName());
            modelMap.put("categoryName", request.getCategoryName() != null && !request.getCategoryName().isBlank()
                    ? request.getCategoryName()
                    : "General");
            modelMap.put("highlights", request.getKeyHighlights() != null && !request.getKeyHighlights().isEmpty()
                    ? String.join(", ", request.getKeyHighlights())
                    : "High quality, modern design, durable, customer favorite");
            modelMap.put("targetAudience", request.getTargetAudience() != null && !request.getTargetAudience().isBlank()
                    ? request.getTargetAudience()
                    : "General consumers");
            modelMap.put("tone", request.getTone() != null && !request.getTone().isBlank()
                    ? request.getTone()
                    : "Professional and engaging");
            modelMap.put("format", format);

            Prompt prompt = promptTemplate.create(modelMap);

            ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
            if (chatResponse == null || chatResponse.getResult() == null
                    || chatResponse.getResult().getOutput() == null) {
                throw new APIException("Received empty response from OpenAI model.");
            }

            String content = chatResponse.getResult().getOutput().getText();
            ProductDescriptionResponse response = converter.convert(content);
            if (response == null) {
                throw new APIException("Failed to parse structured output from AI response.");
            }
            if (response.getProductName() == null || response.getProductName().isBlank()) {
                response.setProductName(request.getProductName());
            }

            return response;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("Failed to generate product description: " + e.getMessage());
        }
    }

    public ProductDescriptionResponse generateDescriptionForExistingProduct(Long productId, String tone,
            String targetAudience) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        ProductDescriptionGenerateRequest request = new ProductDescriptionGenerateRequest();
        request.setProductName(product.getProductName());
        request.setCategoryName(product.getCategory() != null ? product.getCategory().getCategoryName() : "General");
        if (product.getDescription() != null && !product.getDescription().isBlank()) {
            request.setKeyHighlights(List.of(product.getDescription()));
        }
        request.setTone(tone);
        request.setTargetAudience(targetAudience);

        return generateDescription(request);
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.trim().equals("${OPENAI_API_KEY:}")) {
            throw new APIException(
                    "OpenAI API key is missing. Please set the OPENAI_API_KEY environment variable to use AI features.");
        }
    }
}
