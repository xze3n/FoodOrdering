package com.foodordering.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDTO {
    private Long id;
    private String name;
    private BigDecimal price;
}
