package org.qrush.brand.brand.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.qrush.brand.brand.dto.BrandDto;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique= true)
    @NotEmpty(message = "Brand name cannot be null or empty")
    private String name;

    public BrandDto toDto() {
        return BrandDto.builder()
                .name(name)
                .id(id)
                .build();
    }
}


