package org.qrush.brand.unit.restaurant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.qrush.brand.brand.BrandService;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.qrush.brand.brand.helpers.BrandMapper;
import org.qrush.brand.restaurant.RestaurantRepository;
import org.qrush.brand.restaurant.RestaurantService;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.exceptions.RestaurantAlreadyExists;
import org.qrush.brand.restaurant.helpers.RestaurantMapper;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.data.geo.Point;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTests {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private BrandService brandService;

    @Spy
    private RestaurantMapper restaurantMapper;

    @Spy
    private BrandMapper brandMapper;

    @InjectMocks
    private RestaurantService restaurantService;

    private Long brandId;
    private BrandDto brandDto;
    private Brand brand;
    private RestaurantDto restaurantDto;
    private Restaurant restaurant;

    @BeforeEach
    public void setup() {
        brandId = 1L;
        brand = Brand.builder()
                .id(brandId)
                .name("Starbucks")
                .build();
        brandDto = BrandDto.builder()
                .name(brand.getName())
                .id(brand.getId())
                .build();

        restaurantDto = RestaurantDto.builder()
                .name("Starbucks London")
                .address("123 Main St")
                .longitude(90.0)
                .latitude(180.0)
                .brandId(brandId)
                .build();

        restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .address(restaurantDto.getAddress())
                .location(new Point(restaurantDto.getLongitude(), restaurantDto.getLatitude()))
                .brand(brand)
                .build();
    }

    //region CREATE
    @Test
    public void restaurantService_CreateRestaurant_ReturnsRestaurantDto() {
        when(brandService.getBrandById(Mockito.anyLong())).thenReturn(brandDto);
        when(restaurantRepository.save(Mockito.any(Restaurant.class))).thenReturn(restaurant);

        RestaurantDto savedRestaurantDto = restaurantService.createRestaurant(restaurantDto);

        assertNotNull(savedRestaurantDto);
        assertEquals(restaurantDto.getName(), savedRestaurantDto.getName());
        assertEquals(restaurantDto.getAddress(), savedRestaurantDto.getAddress());
        assertEquals(restaurantDto.getBrandId(), savedRestaurantDto.getBrandId());
        assertEquals(restaurantDto.getLatitude(), savedRestaurantDto.getLatitude());
        assertEquals(restaurantDto.getLongitude(), savedRestaurantDto.getLongitude());
    }

    @Test
    public void restaurantService_CreateRestaurant_GivenBrandDoesNotExist_ThrowsNotFoundException() {
        when(brandService.getBrandById(Mockito.anyLong())).thenThrow(BrandNotFoundException.class);

        assertThrows(BrandNotFoundException.class, () -> restaurantService.createRestaurant(restaurantDto));
    }

    @Test
    public void restaurantService_CreateBrand_GivenRestaurantNameAlreadyExistsForBrand_ThrowsRestaurantAlreadyExists() {
        when(brandService.getBrandById(Mockito.anyLong())).thenReturn(brandDto);
        when(restaurantRepository.findByNameAndBrandId(Mockito.anyString(), Mockito.anyLong())).thenReturn(Optional.of(restaurant));

        assertThrows(RestaurantAlreadyExists.class, () -> restaurantService.createRestaurant(restaurantDto));
    }
    //endregion
}
