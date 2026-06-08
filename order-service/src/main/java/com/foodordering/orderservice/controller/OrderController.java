package com.foodordering.orderservice.controller;

import com.foodordering.orderservice.dto.OrderRequestDTO;
import com.foodordering.orderservice.model.Order;
import com.foodordering.orderservice.model.OrderStatus;
import com.foodordering.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    private String getUsernameFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequestDTO request) {
        String username = getUsernameFromSecurityContext();
        Order newOrder = orderService.createOrder(username, request);
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders() {
        String username = getUsernameFromSecurityContext();
        return ResponseEntity.ok(orderService.getOrdersForUser(username));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

}
