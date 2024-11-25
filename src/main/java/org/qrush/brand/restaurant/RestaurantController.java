package org.qrush.brand.restaurant;

import jakarta.validation.Valid;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brand/{brand_id}/restaurant")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping()
    public ResponseEntity<RestaurantDto> createRestaurant(@PathVariable("brand_id") Long brandId, @RequestBody @Valid RestaurantDto restaurantDto) {
        restaurantDto.setBrandId(brandId);
        return new ResponseEntity<>(restaurantService.createRestaurant(restaurantDto), HttpStatus.CREATED);
    }
}
