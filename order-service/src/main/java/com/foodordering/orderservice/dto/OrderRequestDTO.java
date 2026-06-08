package com.foodordering.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @NotEmpty(message = "Order must containt atleast 1 item")
    private List<OrderItemRequestDTO> orderItems;
}
