package org.qrush.brand.restaurant.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.data.geo.Point;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantDto {

    private UUID id;

    @NotEmpty(message = "Restaurant name cannot be null or empty")
    private String name;

    @NotEmpty(message = "Restaurant address cannot be null or empty")
    private String address;

    @NotNull(message = "Restaurant latitude cannot be null")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Restaurant longitude cannot be null")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    private UUID brandId;

    public Restaurant toRestaurant(Brand brand) {
        //ToDo: does this need logic to check brand matches brand id??
        return Restaurant.builder()
                .id(id)
                .name(name)
                .address(address)
                .location(new Point(longitude, latitude))
                .brand(brand)
                .build();

    }
}
