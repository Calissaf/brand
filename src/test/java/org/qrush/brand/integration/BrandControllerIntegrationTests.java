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
import org.springframework.http.ProblemDetail;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    void givenValidBrand_whenCreateBrand_ThenReturnBrand() throws Exception {

        BrandDto createdBrand = performPostRequestExpectedSuccess(BRAND_API_ENDPOINT + "/create", brandDto, BrandDto.class);

        assertNotNull(createdBrand);
        assertEquals("Starbucks", createdBrand.getName());
        assertEquals(1L, createdBrand.getId());
    }

    @Test
    @DisplayName("Exception Test: brand name must not be null")
    void givenNullBrandName_whenCreateBrand_ThenReturnBadRequest() throws Exception {
        brandDto.setName(null);

        ProblemDetail problemDetail = performPostRequestExpectClientError(BRAND_API_ENDPOINT + "/create", brandDto, ProblemDetail.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Validation failed for: name (Brand name cannot be null or empty), ", problemDetail.getDetail());
    }

    @Test
    @DisplayName("Exception Test: brand already exists")
    void givenBrandAlreadyExists_whenCreateBrand_ThenReturnConflict() throws Exception {
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
    void givenValidBrandDto_whenGetBrand_ThenReturnBrand() throws Exception {
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
    void givenBrandDoesNotExist_whenGetBrand_ThenReturnNotFound() throws Exception {

        ProblemDetail problemDetail = performGetRequestExpectClientError(BRAND_API_ENDPOINT + "/1", ProblemDetail.class);

        assertNotNull(problemDetail);
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Brand could not be found", problemDetail.getDetail());
    }
    //endregion

    //region GET "/"
    @Test
    @DisplayName("Happy Path Test: gets response dto")
    void GetAll_ReturnsResponseDto() throws Exception {
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
    //endregion

    //region DELETE "/id/delete"
    @SneakyThrows
    @Test
    @DisplayName("Happy Path Test: deletes brand and returns no content")
    void givenBrandId_WhenBrandExists_DeleteBrand() {
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
    void givenBrandId_WhenBrandDoesNotExists_DeleteBrand() {
        var url = String.format("%s/%s/delete", BRAND_API_ENDPOINT, 1);

        var response = performDeleteRequestExpectClientError(url);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
    //endregion
}
