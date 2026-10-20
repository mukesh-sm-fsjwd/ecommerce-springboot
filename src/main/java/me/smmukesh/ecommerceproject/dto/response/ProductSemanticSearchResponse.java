package me.smmukesh.ecommerceproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSemanticSearchResponse {
    private Long productId;
    private String productName;
    private String categoryName;
    private String description;
    private Double price;
    private Double discount;
    private Double specialPrice;
    private String image;
    private Double relevanceScore;
}
