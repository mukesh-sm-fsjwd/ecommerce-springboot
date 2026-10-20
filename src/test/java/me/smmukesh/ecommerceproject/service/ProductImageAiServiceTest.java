package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImageAiServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductImageAiService productImageAiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productImageAiService, "apiKey", "");
        ReflectionTestUtils.setField(productImageAiService, "imagePath", "images/");
    }

    @Test
    @DisplayName("Should throw APIException when OpenAI API key is missing")
    void shouldThrowExceptionWhenApiKeyMissing() {
        MockMultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "image-content".getBytes());

        assertThatThrownBy(() -> productImageAiService.analyzeImage(file, null))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("OpenAI API key is missing");
    }

    @Test
    @DisplayName("Should throw APIException when file is empty")
    void shouldThrowExceptionWhenFileIsEmpty() {
        ReflectionTestUtils.setField(productImageAiService, "apiKey", "valid-key");
        MockMultipartFile emptyFile = new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> productImageAiService.analyzeImage(emptyFile, null))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Image file cannot be empty");
    }

    @Test
    @DisplayName("Should throw APIException when file is not an image")
    void shouldThrowExceptionWhenFileIsNotImage() {
        ReflectionTestUtils.setField(productImageAiService, "apiKey", "valid-key");
        MockMultipartFile textFile = new MockMultipartFile("image", "test.txt", "text/plain", "hello".getBytes());

        assertThatThrownBy(() -> productImageAiService.analyzeImage(textFile, null))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Uploaded file must be a valid image");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when analyzing non-existent product")
    void shouldThrowExceptionWhenProductNotFound() {
        ReflectionTestUtils.setField(productImageAiService, "apiKey", "valid-key");
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productImageAiService.analyzeExistingProductImage(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
    }

    @Test
    @DisplayName("Should throw APIException when product has only default.png image")
    void shouldThrowExceptionWhenProductHasDefaultImage() {
        ReflectionTestUtils.setField(productImageAiService, "apiKey", "valid-key");

        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Item");
        product.setImage("default.png");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productImageAiService.analyzeExistingProductImage(1L))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("does not have a custom image uploaded yet");
    }
}
