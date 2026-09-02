package me.smmukesh.ecommerceproject.service;

import jakarta.transaction.Transactional;
import me.smmukesh.ecommerceproject.dto.request.OrderDTO;
import me.smmukesh.ecommerceproject.dto.request.OrderItemDTO;
import me.smmukesh.ecommerceproject.dto.request.OrderRequestDTO;
import me.smmukesh.ecommerceproject.exception.APIException;
import me.smmukesh.ecommerceproject.exception.ResourceNotFoundException;
import me.smmukesh.ecommerceproject.model.*;
import me.smmukesh.ecommerceproject.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
                        ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.modelMapper = modelMapper;
        this.cartRepository = cartRepository;
        this.addressRepository = addressRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }


}
