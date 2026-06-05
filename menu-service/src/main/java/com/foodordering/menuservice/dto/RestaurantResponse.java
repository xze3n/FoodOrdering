package com.foodordering.menuservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RestaurantResponse {
    private Long id;
    private String name;
    private String description;
    private List<MenuItemResponse> menuItems;
}
