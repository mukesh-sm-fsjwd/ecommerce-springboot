package me.smmukesh.ecommerceproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private CartDTO cart;
    private ProductRequest products;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
