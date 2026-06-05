package com.foodordering.menuservice.service;

import com.foodordering.menuservice.dto.MenuItemResponse;
import com.foodordering.menuservice.dto.RestaurantRequest;
import com.foodordering.menuservice.dto.RestaurantResponse;
import com.foodordering.menuservice.model.Restaurant;
import com.foodordering.menuservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantResponse create(RestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse update(Long id, RestaurantRequest request) {
        Restaurant restaurant = findById(id);
        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        return toResponse(restaurantRepository.save(restaurant));
    }

    public void delete(Long id) {
        findById(id);
        restaurantRepository.deleteById(id);
    }

    public List<RestaurantResponse> getAll() {
        return restaurantRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public RestaurantResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public List<RestaurantResponse> search(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .toList();
    }

    private Restaurant findById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
    }

    private RestaurantResponse toResponse(Restaurant restaurant) {
        List<MenuItemResponse> items = restaurant.getMenuItems().stream()
                .map(item -> MenuItemResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .build())
                .toList();

        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .menuItems(items)
                .build();
    }
}
