package org.qrush.brand.restaurant.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.springframework.data.geo.Point;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "restaurants",
        uniqueConstraints = @UniqueConstraint(name = "Unique restaurant name for each brand", columnNames = {"name", "brand_id"})
)
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotEmpty(message = "Restaurant name cannot be null or empty")
    private String name;

    @Column(nullable = false)
    @NotEmpty(message = "Restaurant address cannot be null or empty")
    private String address;

    @Column(nullable = false)
    @NotNull(message = "Restaurant location cannot be null")
    private Point location;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    public RestaurantDto toDto() {
        return RestaurantDto.builder()
                .id(id)
                .name(name)
                .address(address)
                .longitude(location.getX())
                .latitude(location.getY())
                .brandId(brand.getId())
                .build();
    }
}
