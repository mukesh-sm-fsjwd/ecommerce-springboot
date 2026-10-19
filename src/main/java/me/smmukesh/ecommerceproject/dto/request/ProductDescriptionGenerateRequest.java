package me.smmukesh.ecommerceproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionGenerateRequest {

    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    private String categoryName;

    private List<String> keyHighlights;

    private String targetAudience;

    private String tone;
}
