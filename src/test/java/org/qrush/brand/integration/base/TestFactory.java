package org.qrush.brand.integration.base;

import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.models.Brand;

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
}
