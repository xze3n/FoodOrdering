package com.foodordering.menuservice.controller;

import com.foodordering.menuservice.dto.RestaurantRequest;
import com.foodordering.menuservice.dto.RestaurantResponse;
import com.foodordering.menuservice.service.RestaurantService;
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
@RequestMapping("/menu/restaurants")
@RequiredArgsConstructor
@Tag(name = "Restaurants", description = "Restaurant management")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantResponse>> getAll() {
        return ResponseEntity.ok(restaurantService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID")
    public ResponseEntity<RestaurantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search restaurants by name")
    public ResponseEntity<List<RestaurantResponse>> search(@RequestParam String name) {
        return ResponseEntity.ok(restaurantService.search(name));
    }

    @PostMapping
    @Operation(summary = "Create a restaurant (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a restaurant (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RestaurantResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody RestaurantRequest request) {
        return ResponseEntity.ok(restaurantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a restaurant (ADMIN only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
