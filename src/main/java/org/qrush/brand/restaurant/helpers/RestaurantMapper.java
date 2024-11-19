package org.qrush.brand.restaurant.helpers;

import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {
    public Restaurant toEntity(RestaurantDto restaurantDto, Brand brand) {
        return Restaurant.builder()
                .name(restaurantDto.getName())
                .brand(brand)
                .address(restaurantDto.getAddress())
                .location(new Point(restaurantDto.getLongitude(), restaurantDto.getLatitude()))
                .build();
    }

    public RestaurantDto toDTO(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .brandId(restaurant.getBrand().getId())
                .address(restaurant.getAddress())
                .latitude(restaurant.getLocation().getY())
                .longitude(restaurant.getLocation().getX())
                .build();
    }
}
