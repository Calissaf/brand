package org.qrush.brand.restaurant;

import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
    Optional<Restaurant> findByNameAndBrandId(String name, UUID brandId);
}
