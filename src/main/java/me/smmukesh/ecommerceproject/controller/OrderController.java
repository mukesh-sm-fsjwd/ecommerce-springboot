package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.dto.request.OrderDTO;
import me.smmukesh.ecommerceproject.dto.request.OrderRequestDTO;
import me.smmukesh.ecommerceproject.service.OrderService;
import me.smmukesh.ecommerceproject.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final AuthUtils authUtils;

    @Autowired
    public OrderController(OrderService orderService,AuthUtils authUtils) {
        this.orderService = orderService;
        this.authUtils = authUtils;
    }

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDTO orderRequestDTO){
        String emailId = authUtils.loggedInEmail();
        OrderDTO orderDTO = orderService.orderProducts(emailId,paymentMethod,orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderDTO);
    }
}
