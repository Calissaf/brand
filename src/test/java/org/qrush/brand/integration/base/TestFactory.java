package org.qrush.brand.integration.base;

import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.dto.RestaurantDto;

public class TestFactory {
    public static final String BRAND_API_ENDPOINT = "/brand";

    public Brand generateBrand() {
        return Brand.builder()
                .name("Starbucks")
                .build();
    }

    public BrandDto generateBrandDto() {
        return BrandDto.builder()
                .name("Starbucks")
                .build();
    }

    public RestaurantDto generateRestaurantDto() {
        return RestaurantDto.builder()
                .name("Starbucks Ipswich")
                .address("123 Main Street")
                .latitude(90.0)
                .longitude(180.0)
                .build();
    }
}
