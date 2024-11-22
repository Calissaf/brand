package org.qrush.brand.restaurant;

import org.qrush.brand.brand.BrandService;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.helpers.BrandMapper;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.qrush.brand.restaurant.exceptions.RestaurantAlreadyExists;
import org.qrush.brand.restaurant.helpers.RestaurantMapper;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.stereotype.Service;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final BrandService brandService;
    private final RestaurantMapper restaurantMapper;
    private final BrandMapper brandMapper;

    public RestaurantService(RestaurantRepository restaurantRepository, BrandService brandService, RestaurantMapper restaurantMapper, BrandMapper brandMapper) {
        this.restaurantRepository = restaurantRepository;
        this.brandService = brandService;
        this.restaurantMapper = restaurantMapper;
        this.brandMapper = brandMapper;
    }

    public RestaurantDto createRestaurant(RestaurantDto restaurantDto) {
        BrandDto brandDto = brandService.getBrandById(restaurantDto.getBrandId());
        Brand brand = brandMapper.toEntity(brandDto);

        if (restaurantRepository.findByNameAndBrandId(restaurantDto.getName(), restaurantDto.getBrandId()).orElse(null) != null) {
            throw new RestaurantAlreadyExists(String.format("Restaurant name already exists for brand %s", brand.getName()));
        }

        Restaurant restaurant = restaurantMapper.toEntity(restaurantDto, brand);

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return restaurantMapper.toDTO(savedRestaurant);
    }
}
