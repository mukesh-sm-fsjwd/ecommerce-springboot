package me.smmukesh.ecommerceproject.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.smmukesh.ecommerceproject.model.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;

    @Email
    private String email;

    private List<OrderItemDTO> orderItems;

    private LocalDate orderDate;

    private PaymentDTO payment;

    private Double totalAmount;

    private OrderStatus orderStatus;

    private Long addressId;
}
