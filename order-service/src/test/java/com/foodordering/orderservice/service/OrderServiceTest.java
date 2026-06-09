package com.foodordering.orderservice.service;

import com.foodordering.orderservice.client.MenuServiceClient;
import com.foodordering.orderservice.dto.MenuItemDTO;
import com.foodordering.orderservice.dto.OrderItemRequestDTO;
import com.foodordering.orderservice.dto.OrderRequestDTO;
import com.foodordering.orderservice.model.Order;
import com.foodordering.orderservice.model.OrderItem;
import com.foodordering.orderservice.model.OrderStatus;
import com.foodordering.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuServiceClient menuServiceClient;

    @InjectMocks
    private OrderService orderService;

    private String testUsername;
    private OrderRequestDTO request;
    private MenuItemDTO mockMenuItem;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        testUsername = "johndoe";


        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO(1L, 10L, 2);
        request = new OrderRequestDTO(List.of(itemRequest));


        mockMenuItem = new MenuItemDTO(10L, "Margherita", new BigDecimal("15.00"));


        savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setUsername(testUsername);
        savedOrder.setStatus(OrderStatus.CREATED);
        savedOrder.setTotalPrice(new BigDecimal("30.00")); // 2 x 15.00

        OrderItem savedItem = new OrderItem();
        savedItem.setMenuItemId(10L);
        savedItem.setQuantity(2);
        savedItem.setPriceAtTimeOfOrder(new BigDecimal("15.00"));
        savedOrder.addItem(savedItem);
    }

    @Test
    void createOrder_shouldReturnSavedOrder_whenValidRequest() {

        when(menuServiceClient.getMenuItem(1L, 10L)).thenReturn(mockMenuItem);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);


        Order response = orderService.createOrder(testUsername, request);


        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getUsername()).isEqualTo("johndoe");
        assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("30.00");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getMenuItemId()).isEqualTo(10L);

        verify(menuServiceClient, times(1)).getMenuItem(1L, 10L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrow_whenMenuItemNotFound() {

        when(menuServiceClient.getMenuItem(1L, 10L))
                .thenThrow(new RuntimeException("Produsul nu a fost găsit în meniul acestui restaurant!"));


        assertThatThrownBy(() -> orderService.createOrder(testUsername, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Produsul nu a fost găsit");


        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersForUser_shouldReturnOrderList_whenUserHasOrders() {
        // Arrange
        when(orderRepository.findByUsername(testUsername)).thenReturn(List.of(savedOrder));

        // Act
        List<Order> result = orderService.getOrdersForUser(testUsername);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo(testUsername);
        assertThat(result.get(0).getTotalPrice()).isEqualByComparingTo("30.00");
    }

    @Test
    void getOrdersForUser_shouldReturnEmptyList_whenUserHasNoOrders() {
        // Arrange
        when(orderRepository.findByUsername("unknown_user")).thenReturn(List.of());

        // Act
        List<Order> result = orderService.getOrdersForUser("unknown_user");

        // Assert
        assertThat(result).isEmpty();
    }

}