package org.qrush.brand.unit.brand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.helpers.BrandMapper;
import org.qrush.brand.brand.models.Brand;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrandMapperTests {

    private BrandMapper brandMapper;
    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    public void setup() {
        brandMapper = new BrandMapper();

        brand = Brand.builder()
                .id(UUID.randomUUID())
                .name("Starbucks")
                .build();

        brandDto = BrandDto.builder()
                .id(UUID.randomUUID())
                .name("Costa")
                .build();

    }

    @Test
    public void brandMapper_ToEntity_ReturnsBrandEntity() {
        Brand brandMapperEntity = brandMapper.toEntity(brandDto);

        assertEquals(brandDto.getId(), brandMapperEntity.getId());
        assertEquals(brandDto.getName(), brandMapperEntity.getName());
    }

    @Test
    public void brandMapper_ToDTO_ReturnsBrandDto() {
        BrandDto brandMapperDTO = brandMapper.toDTO(brand);

        assertEquals(brand.getId(), brandMapperDTO.getId());
        assertEquals(brand.getName(), brandMapperDTO.getName());
    }
}
