package me.smmukesh.ecommerceproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDescriptionResponse {

    private String productName;
    private String shortDescription;
    private String fullDescription;
    private List<String> keyFeatures;
    private List<String> seoTags;
    private String marketingTagline;
}
