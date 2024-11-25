package org.qrush.brand.brand.helpers;

import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.models.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public Brand toEntity(BrandDto brandDto) {
        return brandDto.toBrand();
    }

    public BrandDto toDTO(Brand brand) {
        return brand.toDto();
    }
}
