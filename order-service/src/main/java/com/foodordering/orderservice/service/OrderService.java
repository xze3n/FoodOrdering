package com.foodordering.orderservice.service;

import com.foodordering.orderservice.client.MenuServiceClient;
import com.foodordering.orderservice.dto.MenuItemDTO;
import com.foodordering.orderservice.dto.OrderItemRequestDTO;
import com.foodordering.orderservice.dto.OrderRequestDTO;
import com.foodordering.orderservice.model.Order;
import com.foodordering.orderservice.model.OrderItem;
import com.foodordering.orderservice.model.OrderStatus;
import com.foodordering.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuServiceClient menuServiceClient;

    public OrderService(OrderRepository orderRepository, MenuServiceClient menuServiceClient) {
        this.orderRepository = orderRepository;
        this.menuServiceClient = menuServiceClient;
    }

    @Transactional
    public Order createOrder(String username, OrderRequestDTO request) {
        Order order = new Order();
        order.setUsername(username);
        BigDecimal total = BigDecimal.ZERO;

        for(OrderItemRequestDTO itemRequest : request.getOrderItems()) {
            MenuItemDTO menuItem = menuServiceClient.getMenuItem(itemRequest.getRestaurantId() ,itemRequest.getMenuItemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(menuItem.getId());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtTimeOfOrder(orderItem.getPriceAtTimeOfOrder());

            order.addItem(orderItem);

            //calculate price
            BigDecimal itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            total = total.add(itemTotal);
        }

        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersForUser(String username) {
        return orderRepository.findByUsername(username);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}
