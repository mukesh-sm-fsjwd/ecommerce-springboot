package me.smmukesh.ecommerceproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageAnalysisResponse {
    private String detectedProductType;
    private String visualDescription;
    private String suggestedTitle;
    private String suggestedCategory;
    private List<String> primaryColors;
    private List<String> visualAttributes;
    private List<String> suggestedTags;
    private String imageConsistencyNotes;
}
