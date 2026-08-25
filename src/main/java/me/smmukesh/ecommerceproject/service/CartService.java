package me.smmukesh.ecommerceproject.service;

import me.smmukesh.ecommerceproject.dto.request.CartDTO;
import me.smmukesh.ecommerceproject.dto.request.ProductRequest;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.Cart;
import me.smmukesh.ecommerceproject.model.CartItem;
import me.smmukesh.ecommerceproject.model.Product;
import me.smmukesh.ecommerceproject.repository.CartItemRepository;
import me.smmukesh.ecommerceproject.repository.CartRepository;
import me.smmukesh.ecommerceproject.repository.ProductRepository;
import me.smmukesh.ecommerceproject.utils.AuthUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final AuthUtils authutils;

    private final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       ModelMapper modelMapper,
                       AuthUtils authutils) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.authutils = authutils;
    }

    public CartDTO addProductToCart(Long productId, Integer quantity) {
        logger.debug("Adding Product To Cart");
        //1. Find an Existing Cart or Create One.
        Cart cart = createCart();
        //2. Retrieve Product Details.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","Product Id",productId));
        //! 3. Performing Validations.
        //? 3.1 Checking if the product to be added already exists in the cart.
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
        if(cartItem != null){
            logger.error("Cannot Add Product Because it is already exists in the cart.");
            throw new APIException("Product "+product.getProductName()+" is already Exists in the Cart.");
        }
        //? 3.2 Checking if the quantity for the product that the user is trying to add to the cart exists or not.
        if (product.getQuantity() == 0){
            logger.error("Cannot Add Product Because there is no more stocks left.");
            throw new APIException(product.getProductName()+" has no stock left.");
        }
        //? 3.3 Checking do we have enough stock for the item.
        if(product.getQuantity() < quantity){
            logger.error("Cannot Add Product Because you're requesting the quantity more than the available stock.");
            throw new APIException(product.getProductName()+" has only "+product.getQuantity()+" stocks left");
        }
        //4. Create Cart Item.
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        //5. Save Cart Item.
        cartItemRepository.save(newCartItem);
        //! This below line reduces stock when the product item is in the cart.
//        product.setQuantity(product.getQuantity() - quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);
        //6. Return Updated Cart.
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductRequest> productRequestStream = cartItems.stream()
                .map(item -> {
                    ProductRequest map = modelMapper.map(item.getProduct(),ProductRequest.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                });
        cartDTO.setProducts(productRequestStream.toList());
        return cartDTO;
    }

    private Cart createCart(){
        Optional<Cart> userCart = cartRepository.findCartByEmail(authutils.loggedInEmail());
        if (userCart.isPresent()){
            return userCart.get();
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setCartItems(null);
        cart.setUser(authutils.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return newCart;
    }

    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if(carts.isEmpty()){
            throw new APIException("No Cart Exists");
        }
        List<CartDTO> cartDTOs = carts.stream()
                .map(cart -> {
                   CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                   List<ProductRequest> products = cart.getCartItems()
                           .stream().map(product -> modelMapper.map(product, ProductRequest.class))
                           .toList();
                   cartDTO.setProducts(products);
                   return cartDTO;
                }).toList();
        return cartDTOs;
    }

    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.getCartByEmailAndCartId(emailId,cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart","Cart Id",cartId));
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductRequest> products = cart.getCartItems().stream()
                .map(product -> modelMapper.map(product.getProduct(), ProductRequest.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }
}
