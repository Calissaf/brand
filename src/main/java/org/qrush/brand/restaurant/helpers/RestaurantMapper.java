package org.qrush.brand.restaurant.helpers;

import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    public Restaurant toEntity(RestaurantDto restaurantDto, Brand brand) {
        return restaurantDto.toRestaurant(brand);
    }

    public RestaurantDto toDTO(Restaurant restaurant) {
        return restaurant.toDto();
    }
}
