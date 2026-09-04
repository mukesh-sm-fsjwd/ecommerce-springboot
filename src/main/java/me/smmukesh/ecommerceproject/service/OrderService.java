package me.smmukesh.ecommerceproject.service;

import jakarta.transaction.Transactional;
import me.smmukesh.ecommerceproject.dto.request.OrderDTO;
import me.smmukesh.ecommerceproject.dto.request.OrderItemDTO;
import me.smmukesh.ecommerceproject.dto.request.OrderRequestDTO;
import me.smmukesh.ecommerceproject.dto.response.OrderResponse;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.*;
import me.smmukesh.ecommerceproject.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper modelMapper;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        ModelMapper modelMapper,
                        CartRepository cartRepository,
                        AddressRepository addressRepository,
                        PaymentRepository paymentRepository,
                        ProductRepository productRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.modelMapper = modelMapper;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PLACE ORDER
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public OrderDTO orderProducts(String emailId, String paymentMethod, OrderRequestDTO orderRequestDTO) {
        //! 1. Get User Cart.
        Cart cart = cartRepository.findCartByEmail(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "Email", emailId));
        Long addressId = orderRequestDTO.getAddressId();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "Address Id", addressId));

        //* 2. Create a new order with payment info.
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.PROCESSING);
        order.setAddress(address);

        String pgName = orderRequestDTO.getPgName();
        String pgPaymentId = orderRequestDTO.getPgPaymentId();
        String pgStatus = orderRequestDTO.getPgStatus();
        String pgResponseMessage = orderRequestDTO.getPgResponseMessage();

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        //? 3. Get items from the cart into the order items.
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("Cart is Empty.");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        //! 4. Update Product Stock.
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            //* 5. Clear the cart.
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        //? 6. Send back the order summary.
        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item ->
                orderDTO.getOrderItems().add(
                        modelMapper.map(item, OrderItemDTO.class)
                )
        );
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET LOGGED-IN USER'S ORDERS (paginated)
    // ─────────────────────────────────────────────────────────────────────────

    public OrderResponse getOrdersByUser(String emailId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByEmail(emailId, pageable);

        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(order -> {
                    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
                    dto.setAddressId(order.getAddress() != null ? order.getAddress().getAddressId() : null);
                    List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                            .map(item -> modelMapper.map(item, OrderItemDTO.class))
                            .toList();
                    dto.setOrderItems(itemDTOs);
                    return dto;
                })
                .toList();

        OrderResponse response = new OrderResponse();
        response.setContent(orderDTOs);
        response.setPageNumber(orderPage.getNumber());
        response.setPageSize(orderPage.getSize());
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        response.setLastPage(orderPage.isLast());
        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET SINGLE ORDER BY ID (with ownership check)
    // ─────────────────────────────────────────────────────────────────────────

    public OrderDTO getOrderById(String emailId, Long orderId) {
        Order order = orderRepository.findByOrderIdAndEmail(orderId, emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        dto.setAddressId(order.getAddress() != null ? order.getAddress().getAddressId() : null);
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> modelMapper.map(item, OrderItemDTO.class))
                .toList();
        dto.setOrderItems(itemDTOs);
        return dto;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ADMIN — GET ALL ORDERS (paginated)
    // ─────────────────────────────────────────────────────────────────────────

    public OrderResponse getAllOrders(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                .map(order -> {
                    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
                    dto.setAddressId(order.getAddress() != null ? order.getAddress().getAddressId() : null);
                    List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                            .map(item -> modelMapper.map(item, OrderItemDTO.class))
                            .toList();
                    dto.setOrderItems(itemDTOs);
                    return dto;
                })
                .toList();

        OrderResponse response = new OrderResponse();
        response.setContent(orderDTOs);
        response.setPageNumber(orderPage.getNumber());
        response.setPageSize(orderPage.getSize());
        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        response.setLastPage(orderPage.isLast());
        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  ADMIN — UPDATE ORDER STATUS
    // ─────────────────────────────────────────────────────────────────────────

    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderId", orderId));

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new APIException("Invalid order status: " + status +
                    ". Valid values: PENDING_PAYMENT, PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED");
        }

        Order updatedOrder = orderRepository.save(order);
        OrderDTO dto = modelMapper.map(updatedOrder, OrderDTO.class);
        dto.setAddressId(updatedOrder.getAddress() != null ? updatedOrder.getAddress().getAddressId() : null);
        List<OrderItemDTO> itemDTOs = updatedOrder.getOrderItems().stream()
                .map(item -> modelMapper.map(item, OrderItemDTO.class))
                .toList();
        dto.setOrderItems(itemDTOs);
        return dto;
    }
}
