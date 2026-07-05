package me.smmukesh.ecommerceproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long productId;

    @NotBlank
    @Size(min = 3,message = "Product Name at least should contain 3 characters")
    private String productName;
    private String image;

    @NotBlank
    @Size(min = 5,message = "Product Name at least should contain 5 characters")
    private String description;
    private Integer quantity;
    private Double price;
    private Double discount;
    private Double specialPrice;
}
