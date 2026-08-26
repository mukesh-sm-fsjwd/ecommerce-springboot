package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.dto.request.CartDTO;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Cart;
import me.smmukesh.ecommerceproject.repository.CartRepository;
import me.smmukesh.ecommerceproject.service.CartService;
import me.smmukesh.ecommerceproject.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    private final CartRepository cartRepository;
    private final AuthUtils authutils;

    @Autowired
    public CartController(CartService cartService, CartRepository cartRepository, AuthUtils authutils) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.authutils = authutils;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductsToCart(@PathVariable Long productId,
                                                     @PathVariable Integer quantity){
        CartDTO savedCartDto = cartService.addProductToCart(productId,quantity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedCartDto);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartDTOs);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authutils.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","email",emailId));
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId,cartId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartDTO);
    }

    @PutMapping("/cart/product/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long productId,
                                                     @PathVariable String operation){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete") ? -1 : 1);
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartDTO);
    }


}
