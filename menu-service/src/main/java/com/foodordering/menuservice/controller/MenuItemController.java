package com.foodordering.menuservice.controller;

import com.foodordering.menuservice.dto.MenuItemRequest;
import com.foodordering.menuservice.dto.MenuItemResponse;
import com.foodordering.menuservice.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu/restaurants/{restaurantId}/items")
@RequiredArgsConstructor
@Tag(name = "Menu Items", description = "Menu item management per restaurant")
public class MenuItemController {
    private final MenuItemService menuItemService;

    @GetMapping
    @Operation(summary = "Get all items for a restaurant")
    public ResponseEntity<List<MenuItemResponse>> getItems(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemService.getItemsByRestaurant(restaurantId));
    }

    @PostMapping
    @Operation(summary = "Add a menu item (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MenuItemResponse> addItem(@PathVariable Long restaurantId,
                                                    @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.addItem(restaurantId, request));
    }

    @PutMapping("/{itemId}")
    @Operation(summary = "Update a menu item (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MenuItemResponse> updateItem(@PathVariable Long restaurantId,
                                                       @PathVariable Long itemId,
                                                       @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuItemService.updateItem(restaurantId, itemId, request));
    }

    @DeleteMapping("/{itemId}")
    @Operation(summary = "Delete a menu item (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteItem(@PathVariable Long restaurantId,
                                           @PathVariable Long itemId) {
        menuItemService.deleteItem(restaurantId, itemId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
