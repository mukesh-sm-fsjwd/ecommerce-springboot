package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.response.APIResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductEmbeddingServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductEmbeddingService productEmbeddingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productEmbeddingService, "apiKey", "");
    }

    @Test
    @DisplayName("Should throw APIException when API key is missing on sync")
    void shouldThrowExceptionWhenApiKeyMissingOnSync() {
        assertThatThrownBy(() -> productEmbeddingService.syncProductsToVectorStore())
                .isInstanceOf(APIException.class)
                .hasMessageContaining("OpenAI API key is missing");
    }

    @Test
    @DisplayName("Should throw APIException when API key is missing on search")
    void shouldThrowExceptionWhenApiKeyMissingOnSearch() {
        assertThatThrownBy(() -> productEmbeddingService.searchProductsSemantically("shoes", 5, 0.0))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("OpenAI API key is missing");
    }

    @Test
    @DisplayName("Should return message when no products exist to sync")
    void shouldReturnNoticeWhenNoProductsToSync() {
        ReflectionTestUtils.setField(productEmbeddingService, "apiKey", "test-key");
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        APIResponse response = productEmbeddingService.syncProductsToVectorStore();
        assertThat(response.isStatus()).isFalse();
        assertThat(response.getMessage()).contains("No products found");
    }

    @Test
    @DisplayName("Should throw APIException when query is blank")
    void shouldThrowExceptionWhenQueryIsBlank() {
        ReflectionTestUtils.setField(productEmbeddingService, "apiKey", "test-key");

        assertThatThrownBy(() -> productEmbeddingService.searchProductsSemantically("   ", 5, 0.0))
                .isInstanceOf(APIException.class)
                .hasMessageContaining("Search query cannot be empty");
    }
}
