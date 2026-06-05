package com.foodordering.menuservice.service;

import com.foodordering.menuservice.dto.RestaurantRequest;
import com.foodordering.menuservice.dto.RestaurantResponse;
import com.foodordering.menuservice.model.MenuItem;
import com.foodordering.menuservice.model.Restaurant;
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
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Restaurant savedRestaurant;
    private RestaurantRequest request;

    @BeforeEach
    void setUp() {
        request = new RestaurantRequest();
        request.setName("Pizza Palace");
        request.setDescription("Best pizza in town");

        savedRestaurant = Restaurant.builder()
                .id(1L)
                .name("Pizza Palace")
                .description("Best pizza in town")
                .build();
    }

    @Test
    void create_shouldReturnResponse_whenValidRequest() {
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(savedRestaurant);

        RestaurantResponse response = restaurantService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Pizza Palace");
        assertThat(response.getDescription()).isEqualTo("Best pizza in town");
        assertThat(response.getMenuItems()).isEmpty();
    }

    @Test
    void update_shouldReturnUpdatedResponse_whenRestaurantExists() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(savedRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(savedRestaurant);

        request.setName("Burger Barn");
        savedRestaurant.setName("Burger Barn");

        RestaurantResponse response = restaurantService.update(1L, request);

        assertThat(response.getName()).isEqualTo("Burger Barn");
    }

    @Test
    void update_shouldThrow_whenRestaurantNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.update(99L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void delete_shouldCallRepository_whenRestaurantExists() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(savedRestaurant));

        restaurantService.delete(1L);

        verify(restaurantRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenRestaurantNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.delete(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void getAll_shouldReturnAllRestaurants() {
        Restaurant second = Restaurant.builder().id(2L).name("Sushi Stop").build();
        when(restaurantRepository.findAll()).thenReturn(List.of(savedRestaurant, second));

        List<RestaurantResponse> result = restaurantService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Pizza Palace");
        assertThat(result.get(1).getName()).isEqualTo("Sushi Stop");
    }

    @Test
    void getById_shouldReturnResponse_whenExists() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(savedRestaurant));

        RestaurantResponse response = restaurantService.getById(1L);

        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restaurantService.getById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurant not found");
    }

    @Test
    void search_shouldReturnMatchingRestaurants() {
        when(restaurantRepository.findByNameContainingIgnoreCase("pizza"))
                .thenReturn(List.of(savedRestaurant));

        List<RestaurantResponse> result = restaurantService.search("pizza");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Pizza Palace");
    }

    @Test
    void getById_shouldIncludeMenuItems_whenPresent() {
        MenuItem item = MenuItem.builder()
                .id(10L)
                .name("Margherita")
                .price(new BigDecimal("9.99"))
                .restaurant(savedRestaurant)
                .build();
        savedRestaurant.getMenuItems().add(item);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(savedRestaurant));

        RestaurantResponse response = restaurantService.getById(1L);

        assertThat(response.getMenuItems()).hasSize(1);
        assertThat(response.getMenuItems().get(0).getName()).isEqualTo("Margherita");
        assertThat(response.getMenuItems().get(0).getPrice()).isEqualByComparingTo("9.99");
    }
}
