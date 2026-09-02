package me.smmukesh.ecommerceproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;

    private ProductRequest product;

    private Integer quantity;

    private Double discount;

    private Double orderedProductPrice;
}
