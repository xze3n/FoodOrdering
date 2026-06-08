package com.foodordering.menuservice.service;

import com.foodordering.menuservice.dto.MenuItemRequest;
import com.foodordering.menuservice.dto.MenuItemResponse;
import com.foodordering.menuservice.model.MenuItem;
import com.foodordering.menuservice.model.Restaurant;
import com.foodordering.menuservice.repository.MenuItemRepository;
import com.foodordering.menuservice.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemResponse addItem(Long restaurantId, MenuItemRequest request) {
        Restaurant restaurant = findRestaurant(restaurantId);
        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .price(request.getPrice())
                .restaurant(restaurant)
                .build();
        return toResponse(menuItemRepository.save(item));
    }

    public MenuItemResponse updateItem(Long restaurantId, Long itemId, MenuItemRequest request) {
        findRestaurant(restaurantId);
        MenuItem item = menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        return toResponse(menuItemRepository.save(item));
    }

    public void deleteItem(Long restaurantId, Long itemId) {
        findRestaurant(restaurantId);
        MenuItem item = menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
        menuItemRepository.delete(item);
    }

    public List<MenuItemResponse> getItemsByRestaurant(Long restaurantId) {
        findRestaurant(restaurantId);
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Restaurant findRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build();
    }
}
