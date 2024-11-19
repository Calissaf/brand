package org.qrush.brand.unit.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.restaurant.RestaurantController;
import org.qrush.brand.restaurant.RestaurantService;
import org.qrush.brand.restaurant.dto.RestaurantDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = RestaurantController.class)
@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @Autowired
    private ObjectMapper objectMapper;
    private Brand brand;
    private RestaurantDto restaurantDto;

    @BeforeEach
    public void setup() {
        brand = Brand.builder()
                .id(1L)
                .name("Starbucks")
                .build();



        restaurantDto = RestaurantDto.builder()
                .name("Starbucks Ipswich")
                .address("123 Main Street")
                .latitude(90.0)
                .longitude(180.0)
                .brandId(brand.getId())
                .build();


    }

    //region CREATE
    @Test
    void restaurantController_CreateRestaurant_ReturnsCreatedRestaurant() throws Exception {
        given(restaurantService.createRestaurant(ArgumentMatchers.any())).willAnswer((invocation -> invocation.getArgument(0)));

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
        response.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(restaurantDto)));
    }

    @Test
    void restaurantController_CreateRestaurant_GivenLongitudeOutsideRange_ReturnsBadRequest() throws Exception {
        restaurantDto.setLongitude(200.0);

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.longitude").value("Longitude must be between -180 and 180"));
    }

    @Test
    void restaurantController_CreateRestaurant_GivenLatitudeOutsideRange_ReturnsBadRequest() throws Exception {
        restaurantDto.setLatitude(200.0);

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.latitude").value("Latitude must be between -90 and 90"));
    }

    @Test
    void restaurantController_CreateRestaurant_GivenEmptyName_ReturnsBadRequest() throws Exception {
        restaurantDto.setName("");

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.name").value("Restaurant name cannot be null or empty"));
    }

    @Test
    void restaurantController_CreateRestaurant_GivenEmptyAddress_ReturnsBadRequest() throws Exception {
        restaurantDto.setAddress("");

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.address").value("Restaurant address cannot be null or empty"));
    }

    @Test
    void restaurantController_CreateRestaurant_GivenInvalidJson_ReturnsBadRequest() throws Exception {

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonMap("request", "this doesn't match our schema"))));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.address").value("Restaurant address cannot be null or empty"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.address").value("Restaurant address cannot be null or empty"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.longitude").value("Restaurant longitude cannot be null"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.latitude").value("Restaurant latitude cannot be null"));
    }

    @Test
    void restaurantController_CreateRestaurant_GivenEmptyJson_ReturnsBadRequest() throws Exception {

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.EMPTY_MAP)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.address").value("Restaurant address cannot be null or empty"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.address").value("Restaurant address cannot be null or empty"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.longitude").value("Restaurant longitude cannot be null"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.latitude").value("Restaurant latitude cannot be null"));
    }

    @Test
    void restaurantController_CreateRestaurant_RestaurantServiceFails_ReturnsInternalServerError() throws Exception {
        doThrow(ServiceException.class).when(restaurantService).createRestaurant(restaurantDto);

        ResultActions response = mockMvc.perform(post(String.format("/brand/%s", brand.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantDto)));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
    // endregion
}
