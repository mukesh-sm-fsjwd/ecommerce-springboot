package me.smmukesh.ecommerceproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    private Long categoryId;

    @NotBlank(message = "Category Name should not be blank.")
    @Size(min = 5,message = "Category Name atleast should contain 5 characters")
    private String categoryName;
}
