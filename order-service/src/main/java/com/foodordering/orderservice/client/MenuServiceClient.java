package com.foodordering.orderservice.client;


import com.foodordering.orderservice.dto.MenuItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.awt.*;

@Service
public class MenuServiceClient {
    private final RestClient restClient;

    public MenuServiceClient(@Value("${menu.service.url}") String menuServiceUrl){
        this.restClient = RestClient.builder().baseUrl(menuServiceUrl).build();
    }

    public MenuItemDTO getMenuItem(Long restaurantId, Long menuItemId) {
        MenuItemDTO[] items = restClient.get()
                .uri("/menu/restaurants/{restaurantId}/items", restaurantId)
                .retrieve()
                .body(MenuItemDTO[].class);

        if (items != null) {
            for(MenuItemDTO item : items){
                if(item.getId().equals(menuItemId)){
                    return item;
                }
            }
        }
        throw new RuntimeException("menu item not found in this restaurant");
    }
}
