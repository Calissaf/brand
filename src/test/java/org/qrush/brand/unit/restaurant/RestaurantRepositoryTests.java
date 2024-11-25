package org.qrush.brand.unit.restaurant;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qrush.brand.brand.BrandRepository;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.RestaurantRepository;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.geo.Point;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
public class RestaurantRepositoryTests {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private BrandRepository brandRepository;

    private Restaurant restaurant;
    private Brand brand;

    @BeforeEach
    public void setup() {
        brand = Brand.builder()
                .name("Starbucks")
                .id(1L)
                .build();

        restaurant = Restaurant.builder()
                .name("Starbucks Ipswich")
                .address("123 Street")
                .location(new Point(80.0, 80.0))
                .brand(brand)
                .build();

    }

    @Test
    public void restaurantRepository_Save_ReturnsSavedRestaurant() {
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        assertNotNull(savedRestaurant);
        assertNotNull(savedRestaurant.getId());
        assertEquals(restaurant.getName(), savedRestaurant.getName());
        assertEquals(restaurant.getAddress(), savedRestaurant.getAddress());
        assertEquals(restaurant.getLocation(), savedRestaurant.getLocation());
        assertEquals(restaurant.getBrand(), savedRestaurant.getBrand());
    }

    @Test
    public void restaurantRepository_Save_GivenEmptyRestaurantName_ThrowsConstraintViolationException() {
        restaurant.setName("");

        var constraintViolationException  = assertThrows(ConstraintViolationException.class, () -> restaurantRepository.saveAndFlush(restaurant));
        var exceptionMessage = constraintViolationException.getConstraintViolations().stream().findFirst().get().getMessage();

        assertEquals(exceptionMessage, "Restaurant name cannot be null or empty");
    }

    @Test
    public void restaurantRepository_Save_GivenNullRestaurantName_ThrowsConstraintViolationException() {
        restaurant.setName(null);

        var constraintViolationException  = assertThrows(ConstraintViolationException.class, () -> restaurantRepository.saveAndFlush(restaurant));
        var exceptionMessage = constraintViolationException.getConstraintViolations().stream().findFirst().get().getMessage();

        assertEquals(exceptionMessage, "Restaurant name cannot be null or empty");
    }

    @Test
    public void restaurantRepository_Save_GivenEmptyRestaurantAddress_ThrowsConstraintViolationException() {
        restaurant.setAddress("");

        var constraintViolationException  = assertThrows(ConstraintViolationException.class, () -> restaurantRepository.saveAndFlush(restaurant));
        var exceptionMessage = constraintViolationException.getConstraintViolations().stream().findFirst().get().getMessage();

        assertEquals(exceptionMessage, "Restaurant address cannot be null or empty");
    }

    @Test
    public void restaurantRepository_Save_GivenNullRestaurantAddress_ThrowsConstraintViolationException() {
        restaurant.setAddress(null);

        var constraintViolationException  = assertThrows(ConstraintViolationException.class, () -> restaurantRepository.saveAndFlush(restaurant));
        var exceptionMessage = constraintViolationException.getConstraintViolations().stream().findFirst().get().getMessage();

        assertEquals(exceptionMessage, "Restaurant address cannot be null or empty");
    }

    @Test
    public void restaurantRepository_Save_GivenNullRestaurantLocation_ThrowsConstraintViolationException() {
        restaurant.setLocation(null);

        var constraintViolationException  = assertThrows(ConstraintViolationException.class, () -> restaurantRepository.saveAndFlush(restaurant));
        var exceptionMessage = constraintViolationException.getConstraintViolations().stream().findFirst().get().getMessage();

        assertEquals(exceptionMessage, "Restaurant location cannot be null");
    }

    @Test
    public void restaurantRepository_Save_GivenRestaurantNameAlreadyExistsForBrand_ThrowsDataIntegrityException() {
        Brand savedBrand = brandRepository.save(brand);
        restaurant.setBrand(savedBrand);

        restaurantRepository.save(restaurant);
        Restaurant duplicateRestaurant = Restaurant.builder()
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .location(restaurant.getLocation())
                .brand(restaurant.getBrand())
                .build();

       var dataIntegrityViolationException =  assertThrows(DataIntegrityViolationException.class, () -> restaurantRepository.saveAndFlush(duplicateRestaurant));
       var localizedMessage = dataIntegrityViolationException.getCause().getLocalizedMessage();

       assertTrue(localizedMessage.contains("Unique restaurant name for each brand"), "Data integrity violation on unique constraint unique restaurant name per brand");
    }

    @Test
    public void restaurantRepository_FindByNameAndBrandId_ReturnsRestaurant() {
        Brand savedBrand = brandRepository.save(brand);

        restaurant.setBrand(savedBrand);
        restaurantRepository.save(restaurant);

        Restaurant foundRestaurant = restaurantRepository.findByNameAndBrandId(restaurant.getName(), restaurant.getBrand().getId()).orElse(null);

        assertNotNull(foundRestaurant);
        assertNotNull(foundRestaurant.getId());
        assertEquals(restaurant.getName(), foundRestaurant.getName());
        assertEquals(restaurant.getAddress(), foundRestaurant.getAddress());
        assertEquals(restaurant.getLocation(), foundRestaurant.getLocation());
        assertEquals(restaurant.getBrand(), foundRestaurant.getBrand());
    }

    @Test
    public void restaurantRepository_FindByNameAndBrandId_GivenRestaurantNameDoesntExist_ReturnsNull() {
        brandRepository.save(brand);
        Restaurant foundRestaurant = restaurantRepository.findByNameAndBrandId(restaurant.getName(), restaurant.getBrand().getId()).orElse(null);

        assertNull(foundRestaurant);
    }

    @Test
    public void restaurantRepository_FindByNameAndBrandId_GivenBrandDoesntExist_ReturnsNull() {
        Restaurant foundRestaurant = restaurantRepository.findByNameAndBrandId(restaurant.getName(), restaurant.getBrand().getId()).orElse(null);

        assertNull(foundRestaurant);
    }
}

