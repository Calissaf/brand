package org.qrush.brand.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import org.qrush.brand.brand.models.Brand;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {

    private Long id;

    @NotEmpty(message = "Brand name cannot be null or empty")
    private String name;

    public Brand toBrand () {
        return Brand.builder()
                .name(name)
                .id(id)
                .build();
    }
}
