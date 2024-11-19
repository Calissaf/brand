package org.qrush.brand.unit.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.qrush.brand.restaurant.helpers.RestaurantMapper;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.data.geo.Point;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestaurantMapperTests {

    private RestaurantMapper restaurantMapper;
    private RestaurantDto restaurantDto;
    private Restaurant restaurant;
    private Brand brand;

    @BeforeEach
    public void setup() {
        restaurantMapper = new RestaurantMapper();

        brand = Brand.builder()
                .id(UUID.randomUUID())
                .name("Starbucks")
                .build();

        restaurantDto = RestaurantDto.builder()
                .id(null)
                .name("Starbucks Ipswich")
                .brandId(UUID.randomUUID())
                .address("123 Street")
                .latitude(1.0)
                .longitude(50.0)
                .build();

        restaurant = Restaurant.builder()
                .name("Starbucks Ipswich")
                .brand(brand)
                .address("123 Street")
                .location(new Point(1.0, 50.0))
                .build();
    }

    @Test
    public void restaurantMapper_ToEntity_ReturnsRestaurantEntity() {
        Restaurant restaurantEntity = restaurantMapper.toEntity(restaurantDto, brand);

        assertEquals(restaurantDto.getName(), restaurantEntity.getName());
        assertEquals(brand, restaurantEntity.getBrand());
        assertEquals(restaurantDto.getAddress(), restaurantEntity.getAddress());
        assertEquals(restaurantDto.getLatitude(), restaurantEntity.getLocation().getY());
        assertEquals(restaurantDto.getLongitude(), restaurantEntity.getLocation().getX());
    }

    @Test
    public void restaurantMapper_ToDTO_ReturnsRestaurantDto() {
        RestaurantDto restaurantDto = restaurantMapper.toDTO(restaurant);

        assertEquals(restaurant.getId(), restaurantDto.getId());
        assertEquals(restaurant.getName(), restaurantDto.getName());
        assertEquals(restaurant.getBrand().getId(), restaurantDto.getBrandId());
        assertEquals(restaurant.getAddress(), restaurantDto.getAddress());
        assertEquals(restaurant.getLocation().getY(), restaurantDto.getLatitude());
        assertEquals(restaurant.getLocation().getX(), restaurantDto.getLongitude());
    }
}
