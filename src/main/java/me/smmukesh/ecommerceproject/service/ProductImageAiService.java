package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.response.ProductImageAnalysisResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ProductImageAiService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;

    @Value("${project.image:images/}")
    private String imagePath;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Autowired
    public ProductImageAiService(ChatClient chatClient, ProductRepository productRepository) {
        this.chatClient = chatClient;
        this.productRepository = productRepository;
    }

    public ProductImageAnalysisResponse analyzeImage(MultipartFile file, String contextHint) {
        validateApiKey();

        if (file == null || file.isEmpty()) {
            throw new APIException("Image file cannot be empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new APIException("Uploaded file must be a valid image (e.g. image/jpeg, image/png, image/webp).");
        }

        Resource resource = file.getResource();
        MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);

        return processImageAnalysis(resource, mimeType, contextHint);
    }

    public ProductImageAnalysisResponse analyzeExistingProductImage(Long productId) {
        validateApiKey();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        String imageName = product.getImage();
        if (imageName == null || imageName.isBlank() || imageName.equalsIgnoreCase("default.png")) {
            throw new APIException("Product does not have a custom image uploaded yet (current image: '" + imageName + "').");
        }

        Path fileOnDisk = Path.of(imagePath, imageName);
        if (!Files.exists(fileOnDisk)) {
            throw new APIException("Product image file not found on server disk: " + fileOnDisk.toAbsolutePath());
        }

        try {
            String probeType = Files.probeContentType(fileOnDisk);
            if (probeType == null || !probeType.startsWith("image/")) {
                probeType = "image/jpeg";
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(probeType);
            Resource resource = new FileSystemResource(fileOnDisk);

            String contextHint = "Product Name: " + product.getProductName()
                    + ", Category: " + (product.getCategory() != null ? product.getCategory().getCategoryName() : "General")
                    + ", Current Description: " + (product.getDescription() != null ? product.getDescription() : "");

            return processImageAnalysis(resource, mimeType, contextHint);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("Failed to read product image: " + e.getMessage());
        }
    }

    private ProductImageAnalysisResponse processImageAnalysis(Resource resource, MimeType mimeType, String contextHint) {
        try {
            BeanOutputConverter<ProductImageAnalysisResponse> converter =
                    new BeanOutputConverter<>(ProductImageAnalysisResponse.class);
            String format = converter.getFormat();

            String promptText = """
                    You are an expert e-commerce catalog auditor and product imagery specialist.
                    Analyze the attached product image in detail and extract structured product information.

                    Context / Reference information:
                    %s

                    Task:
                    1. Detect the exact product type and summarize what is visible in the image.
                    2. Provide a vivid, accurate visual description of packaging, design, colors, and styling.
                    3. Suggest a professional, high-converting product title and appropriate store category.
                    4. Identify the primary colors and key visual attributes.
                    5. Provide recommended SEO tags based on visual elements.
                    6. Note any consistency or quality observations in 'imageConsistencyNotes'.

                    Ensure your response strictly complies with this JSON schema:
                    %s
                    """.formatted(contextHint != null && !contextHint.isBlank() ? contextHint : "None provided", format);

            ChatResponse response = chatClient.prompt()
                    .user(u -> u.text(promptText).media(mimeType, resource))
                    .call()
                    .chatResponse();

            if (response == null || response.getResult() == null || response.getResult().getOutput() == null) {
                throw new APIException("Received empty response from vision model.");
            }

            String content = response.getResult().getOutput().getText();
            ProductImageAnalysisResponse analysisResponse = converter.convert(content);
            if (analysisResponse == null) {
                throw new APIException("Failed to parse structured output from image analysis.");
            }

            return analysisResponse;
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("Multimodal image analysis failed: " + e.getMessage());
        }
    }

    private void validateApiKey() {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.trim().equals("${OPENAI_API_KEY:}")) {
            throw new APIException("OpenAI API key is missing. Please set the OPENAI_API_KEY environment variable to use AI features.");
        }
    }
}
