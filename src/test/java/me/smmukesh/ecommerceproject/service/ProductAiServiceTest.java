package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.ProductDescriptionGenerateRequest;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductAiServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductAiService productAiService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productAiService, "apiKey", "");
    }

    @Test
    @DisplayName("Should throw APIException when OpenAI API key is missing")
    void shouldThrowExceptionWhenApiKeyMissing() {
        ProductDescriptionGenerateRequest request = new ProductDescriptionGenerateRequest();
        request.setProductName("Test Running Shoes");

        assertThatThrownBy(() -> productAiService.generateDescription(request))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("OpenAI API key is missing");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product does not exist")
    void shouldThrowExceptionWhenProductNotFound() {
        ReflectionTestUtils.setField(productAiService, "apiKey", "test-valid-key");
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productAiService.generateDescriptionForExistingProduct(999L, "Casual", "Athletes"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product");
    }
}
