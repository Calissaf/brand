package org.qrush.brand.integration;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.qrush.brand.brand.BrandRepository;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.dto.BrandResponse;
import org.qrush.brand.brand.models.Brand;
import org.qrush.brand.integration.base.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BrandControllerIntegrationTests extends AbstractIntegrationTest {

    @Autowired
    private BrandRepository brandRepository;
    private BrandDto brandDto;

    @PostConstruct
    public void init() {
        brandDto = generateBrandDto();
    }

    //region POST "/create"
    @Test
    @DisplayName("Happy Path Test: create and return brand")
    void brandControllerIntegration_CreateBrand_ReturnsBrandDto() throws Exception {

        BrandDto createdBrand = performPostRequestExpectedSuccess(BRAND_API_ENDPOINT + "/create", brandDto, BrandDto.class);

        assertNotNull(createdBrand);
        assertEquals("Starbucks", createdBrand.getName());
        assertEquals(1L, createdBrand.getId());
    }

    @Test
    @DisplayName("Exception Test: brand name must not be null")
    void brandControllerIntegration_CreateBrand_GivenNullBrandName_ReturnsBadRequest() throws Exception {
        brandDto.setName(null);

        ProblemDetail problemDetail = performPostRequestExpectClientError(BRAND_API_ENDPOINT + "/create", brandDto, ProblemDetail.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
    }

    @Test
    @DisplayName("Exception Test: brand name must not be empty")
    void brandControllerIntegration_CreateBrand_GivenEmptyBrandName_ReturnsBadRequest() throws Exception {
        brandDto.setName("");

        ProblemDetail problemDetail = performPostRequestExpectClientError(BRAND_API_ENDPOINT + "/create", brandDto, ProblemDetail.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Invalid request parameters", problemDetail.getDetail());
    }

    @Test
    @DisplayName("Exception Test: brand already exists")
    void brandControllerIntegration_CreateBrand_GivenBrandAlreadyExists_ReturnsConflict() throws Exception {
        Brand brand = generateBrand();
        brandRepository.save(brand);

        ProblemDetail problemDetail = performPostRequestExpectClientError(BRAND_API_ENDPOINT + "/create", brandDto, ProblemDetail.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.CONFLICT.value(), problemDetail.getStatus());
        assertEquals("Brand name already exists", problemDetail.getDetail());
    }
    //endregion

    //region GET "/id"
    @Test
    @DisplayName("Happy Path Test: gets brand dto")
    void brandControllerIntegration_GetBrandById_ReturnsBrandDto() throws Exception {
        Brand brand = generateBrand();
        brandRepository.save(brand);

        String id = brand.getId().toString();

        BrandDto brandResponse = performGetRequestExpectedSuccess(BRAND_API_ENDPOINT + "/" + id , BrandDto.class);

        assertNotNull(brandResponse);
        assertEquals("Starbucks", brandResponse.getName());
        assertEquals(1L, brandResponse.getId());
    }

    @Test
    @DisplayName("Exception Test: brand does not exist")
    void brandControllerIntegration_GetBrandById_WhenBrandNotFound_ReturnsNotFound() throws Exception {

        ProblemDetail problemDetail = performGetRequestExpectClientError(BRAND_API_ENDPOINT + "/1", ProblemDetail.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Brand could not be found", problemDetail.getDetail());
    }
    //endregion

    //region GET "/"
    @Test
    @DisplayName("Happy Path Test: gets response dto")
    void brandControllerIntegration_GetAllBrands_ReturnsResponseDto() throws Exception {
        int pageNo = 0;
        int pageSize = 10;

        Brand brand = generateBrand();
        brandRepository.save(brand);

        BrandDto brandDtoExpectedResponse = BrandDto.builder()
                .name(brand.getName())
                .id(brand.getId())
                .build();

        var uri = UriComponentsBuilder.fromUri(new URI(BRAND_API_ENDPOINT))
                .queryParam("pageNo", pageNo)
                .queryParam("pageSize", pageSize)
                .build().toUri()
                .toString();

        BrandResponse brandResponse = performGetRequestExpectedSuccess(uri, BrandResponse.class);

        assertNotNull(brandResponse);
        assertEquals(pageNo, brandResponse.getPageNumber());
        assertEquals(pageSize, brandResponse.getPageSize());
        assertEquals(brandDtoExpectedResponse, brandResponse.getContent().getFirst());
    }

    @Test
    @DisplayName("Exception Test: when no brands found returns no content")
    void brandControllerIntegration_GetAllBrands_WhenBrandNotFound_ReturnsNoContent() throws Exception {
        int pageNo = 1;
        int pageSize = 10;

        Brand brand = generateBrand();
        brandRepository.save(brand);

        var uri = UriComponentsBuilder.fromUri(new URI(BRAND_API_ENDPOINT))
                .queryParam("pageNo", pageNo)
                .queryParam("pageSize", pageSize)
                .build().toUri()
                .toString();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        assertEquals("", result.getResponse().getContentAsString());
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }
    //endregion

    //region DELETE "/id/delete"
    @SneakyThrows
    @Test
    @DisplayName("Happy Path Test: deletes brand and returns no content")
    void brandControllerIntegration_DeleteBrandById_ReturnsNoContent() throws Exception {
        Brand brand = generateBrand();
        brandRepository.save(brand);

        var url = String.format("%s/%s/delete", BRAND_API_ENDPOINT, brand.getId());

        var response = performDeleteRequestExpectedSuccess(url);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @SneakyThrows
    @Test
    @DisplayName("ExceptionTest: when brand does not exist returns not found")
    void brandControllerIntegration_DeleteBrandById_WhenBrandNotFound_ReturnsNotFound() throws Exception {
        var url = String.format("%s/%s/delete", BRAND_API_ENDPOINT, 1);

        var response = performDeleteRequestExpectClientError(url);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
    //endregion
}
