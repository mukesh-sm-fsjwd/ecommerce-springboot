package me.smmukesh.ecommerceproject.controller;

import me.smmukesh.ecommerceproject.config.AppConstants;
import me.smmukesh.ecommerceproject.dto.request.OrderDTO;
import me.smmukesh.ecommerceproject.dto.request.OrderRequestDTO;
import me.smmukesh.ecommerceproject.dto.response.OrderResponse;
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
    public OrderController(OrderService orderService, AuthUtils authUtils) {
        this.orderService = orderService;
        this.authUtils = authUtils;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  POST /api/order/users/payments/{paymentMethod}
    //  Place a new order (authenticated user)
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
            @PathVariable String paymentMethod,
            @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtils.loggedInEmail();
        OrderDTO orderDTO = orderService.orderProducts(emailId, paymentMethod, orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/orders/my
    //  Get current user's orders (paginated)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/orders/my")
    public ResponseEntity<OrderResponse> getMyOrders(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize) {
        String emailId = authUtils.loggedInEmail();
        OrderResponse orderResponse = orderService.getOrdersByUser(emailId, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/orders/{orderId}
    //  Get a specific order by ID (must belong to logged-in user)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        String emailId = authUtils.loggedInEmail();
        OrderDTO orderDTO = orderService.getOrderById(emailId, orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderDTO);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  GET /api/admin/orders
    //  Admin — Get all orders (paginated)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize) {
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(orderResponse);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUT /api/admin/orders/{orderId}/status/{status}
    //  Admin — Update order status
    // ─────────────────────────────────────────────────────────────────────────

    @PutMapping("/admin/orders/{orderId}/status/{status}")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @PathVariable String status) {
        OrderDTO orderDTO = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.status(HttpStatus.OK).body(orderDTO);
    }
}
