package org.qrush.brand.brand.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandDto {

    private UUID id;

    @NotEmpty(message = "Brand name cannot be null or empty")
    private String name;
}
