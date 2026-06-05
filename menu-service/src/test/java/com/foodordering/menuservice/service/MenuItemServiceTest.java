package com.foodordering.menuservice.service;

import com.foodordering.menuservice.dto.MenuItemRequest;
import com.foodordering.menuservice.dto.MenuItemResponse;
import com.foodordering.menuservice.model.MenuItem;
import com.foodordering.menuservice.model.Restaurant;
import com.foodordering.menuservice.repository.MenuItemRepository;
import com.foodordering.menuservice.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    private Restaurant restaurant;
    private MenuItem savedItem;
    private MenuItemRequest request;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .build();

        request = new MenuItemRequest();
        request.setName("Margherita");
        request.setPrice(new BigDecimal("9.99"));

        savedItem = MenuItem.builder()
                .id(10L)
                .name("Margherita")
                .price(new BigDecimal("9.99"))
                .restaurant(restaurant)
                .build();
    }

    @Test
    void addItem_shouldReturnResponse_whenRestaurantExists() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(savedItem);

        MenuItemResponse response = menuItemService.addItem(1L, request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getName()).isEqualTo("Margherita");
        assertThat(response.getPrice()).isEqualByComparingTo("9.99");
    }

    @Test
    void addItem_shouldThrow_whenRestaurantNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.addItem(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void updateItem_shouldReturnUpdatedResponse_whenItemExists() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByIdAndRestaurantId(10L, 1L)).thenReturn(Optional.of(savedItem));

        request.setName("Pepperoni");
        request.setPrice(new BigDecimal("12.50"));
        savedItem.setName("Pepperoni");
        savedItem.setPrice(new BigDecimal("12.50"));
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(savedItem);

        MenuItemResponse response = menuItemService.updateItem(1L, 10L, request);

        assertThat(response.getName()).isEqualTo("Pepperoni");
        assertThat(response.getPrice()).isEqualByComparingTo("12.50");
    }

    @Test
    void updateItem_shouldThrow_whenItemNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByIdAndRestaurantId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.updateItem(1L, 99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Menu item not found");
    }

    @Test
    void deleteItem_shouldCallRepository_whenItemExists() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByIdAndRestaurantId(10L, 1L)).thenReturn(Optional.of(savedItem));

        menuItemService.deleteItem(1L, 10L);

        verify(menuItemRepository).delete(savedItem);
    }

    @Test
    void deleteItem_shouldThrow_whenItemNotFound() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByIdAndRestaurantId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.deleteItem(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Menu item not found");
    }

    @Test
    void getItemsByRestaurant_shouldReturnItems_whenRestaurantExists() {
        MenuItem second = MenuItem.builder().id(11L).name("BBQ Chicken").price(new BigDecimal("11.00"))
                .restaurant(restaurant).build();

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByRestaurantId(1L)).thenReturn(List.of(savedItem, second));

        List<MenuItemResponse> result = menuItemService.getItemsByRestaurant(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Margherita");
        assertThat(result.get(1).getName()).isEqualTo("BBQ Chicken");
    }

    @Test
    void getItemsByRestaurant_shouldThrow_whenRestaurantNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuItemService.getItemsByRestaurant(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant not found");
    }
}
