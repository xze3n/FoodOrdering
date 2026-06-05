package com.foodordering.menuservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestaurantRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
