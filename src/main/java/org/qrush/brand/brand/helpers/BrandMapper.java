package org.qrush.brand.brand.helpers;

import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.models.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public Brand toEntity(BrandDto brandDto) {
        return Brand.builder()
                .name(brandDto.getName())
                .id(brandDto.getId())
                .build();
    }

    public BrandDto toDTO(Brand brand) {
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }
}
