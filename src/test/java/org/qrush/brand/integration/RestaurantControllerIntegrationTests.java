package org.qrush.brand.integration;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.*;
import org.qrush.brand.brand.BrandRepository;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.integration.base.AbstractIntegrationTest;
import org.qrush.brand.integration.base.ExtendedProblemDetails;
import org.qrush.brand.restaurant.RestaurantRepository;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.qrush.brand.restaurant.models.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RestaurantControllerIntegrationTests extends AbstractIntegrationTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private BrandRepository brandRepository;

    private RestaurantDto restaurantDto;
    private Brand brand;
    private Brand savedBrand;
    private String url;

    @PostConstruct
    public void setup() {
        restaurantDto = generateRestaurantDto();
        brand = generateBrand();
        savedBrand = brandRepository.save(brand);
        url = BRAND_API_ENDPOINT + "/" + savedBrand.getId().toString() + "/restaurant";
    }

    // region POST
    @Test
    @DisplayName("Happy Path Test: create and return restaurant")
    void restaurantControllerIntegration_CreateRestaurant_ReturnsRestaurantDto() throws Exception {

        RestaurantDto createdRestaurant = performPostRequestExpectedSuccess(url, restaurantDto, RestaurantDto.class);

        assertNotNull(createdRestaurant);
        assertNotNull(createdRestaurant.getId());
        assertEquals(restaurantDto.getName(), createdRestaurant.getName());
        assertEquals(restaurantDto.getAddress(), createdRestaurant.getAddress());
        assertEquals(restaurantDto.getLatitude(), createdRestaurant.getLatitude());
        assertEquals(restaurantDto.getLongitude(), createdRestaurant.getLongitude());
        assertEquals(savedBrand.getId(), createdRestaurant.getBrandId());
    }

    @Test
    @DisplayName("Exception Test: restaurant name must not be empty")
    void restaurantControllerIntegration_CreateRestaurant_GivenEmptyRestaurantName_ReturnsBadRequest() throws Exception {
        restaurantDto.setName("");

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Restaurant name cannot be null or empty", problemDetail.invalidParams.get("name"));
    }

    @Test
    @DisplayName("Exception Test: restaurant name must not be null")
    void restaurantControllerIntegration_CreateRestaurant_GivenNullRestaurantName_ReturnsBadRequest() throws Exception {
        restaurantDto.setName(null);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Restaurant name cannot be null or empty", problemDetail.invalidParams.get("name"));
    }

    @Test
    @DisplayName("Exception Test: restaurant address must not be empty")
    void restaurantControllerIntegration_CreateRestaurant_GivenEmptyRestaurantAddress_ReturnsBadRequest() throws Exception {
        restaurantDto.setAddress("");

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Restaurant address cannot be null or empty", problemDetail.invalidParams.get("address"));
    }

    @Test
    @DisplayName("Exception Test: restaurant address must not be null")
    void restaurantControllerIntegration_CreateRestaurant_GivenNullRestaurantAddress_ReturnsBadRequest() throws Exception {
        restaurantDto.setAddress(null);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Restaurant address cannot be null or empty", problemDetail.invalidParams.get("address"));
    }

    @Test
    @DisplayName("Exception Test: restaurant longitude must not be null")
    void restaurantControllerIntegration_CreateRestaurant_GivenNullRestaurantLongitude_ReturnsBadRequest() throws Exception {
        restaurantDto.setLongitude(null);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Restaurant longitude cannot be null", problemDetail.invalidParams.get("longitude"));
    }

    @Test
    @DisplayName("Exception Test: restaurant address must not be null")
    void restaurantControllerIntegration_CreateRestaurant_GivenNullRestaurantLatitude_ReturnsBadRequest() throws Exception {
        restaurantDto.setLatitude(null);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Restaurant latitude cannot be null", problemDetail.invalidParams.get("latitude"));
    }

    @Test
    @DisplayName("Exception Test: restaurant longitude must be <= 180")
    void restaurantControllerIntegration_CreateRestaurant_GivenRestaurantLongitudeGreaterThan180_ReturnsBadRequest() throws Exception {
        restaurantDto.setLongitude(181.0);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Longitude must be between -180 and 180", problemDetail.invalidParams.get("longitude"));
    }

    @Test
    @DisplayName("Exception Test: restaurant longitude must be => -180")
    void restaurantControllerIntegration_CreateRestaurant_GivenRestaurantLongitudeLessThanNegative180_ReturnsBadRequest() throws Exception {
        restaurantDto.setLongitude(-181.0);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Longitude must be between -180 and 180", problemDetail.invalidParams.get("longitude"));
    }

    @Test
    @DisplayName("Exception Test: restaurant latitude must be <= 90")
    void restaurantControllerIntegration_CreateRestaurant_GivenRestaurantLatitudeGreaterThan90_ReturnsBadRequest() throws Exception {
        restaurantDto.setLatitude(91.0);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Latitude must be between -90 and 90", problemDetail.invalidParams.get("latitude"));
    }

    @Test
    @DisplayName("Exception Test: restaurant latitude must be => -90")
    void restaurantControllerIntegration_CreateRestaurant_GivenRestaurantLatitudeLessThanNegative90_ReturnsBadRequest() throws Exception {
        restaurantDto.setLatitude(-91.0);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
        assertEquals("Latitude must be between -90 and 90", problemDetail.invalidParams.get("latitude"));
    }

    @Test
    @DisplayName("Exception Test: restaurant name must be unique to brand")
    void restaurantControllerIntegration_CreateRestaurant_GivenRestaurantNameAlreadyExistsForBrand_ReturnsConflict() throws Exception {
        Restaurant restaurant = Restaurant.builder()
                .name(restaurantDto.getName())
                .address("456 Main Street")
                .location(new Point(80, 80))
                .brand(savedBrand)
                .build();

        restaurantRepository.save(restaurant);

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(url, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals(String.format("Restaurant name already exists for brand %s", savedBrand.getName()), problemDetail.getDetail());
    }

    @Test
    @DisplayName("Exception Test: brand must exist")
    void restaurantControllerIntegration_CreateRestaurant_GivenBrandDoesNotExist_ReturnsNotFound() throws Exception {
        Long id = savedBrand.getId();
        brandRepository.delete(savedBrand);

        var invalidBrandUrl = BRAND_API_ENDPOINT + "/" + id + "/restaurant";

        ExtendedProblemDetails problemDetail = performPostRequestExpectClientError(invalidBrandUrl, restaurantDto, ExtendedProblemDetails.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Brand could not be found", problemDetail.getDetail());
    }
    //endregion
}

