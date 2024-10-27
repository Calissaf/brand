package org.qrush.brand.brand;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.service.spi.ServiceException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BrandControllerTests {

    @Mock
    private BrandService brandService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private BrandController brandController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    }

    //region CREATE
    // happy path
    // whenBrandSuccessfullyCreated_Return201
    // whenBrandSuccessfullyCreated_ReturnNewBrand
    // unhappy path
    // whenBrandServiceThrowsException_Return500
    // whenBrandRequestInvalid_Return400

    @Test
    void createBrand_WhenBrandSuccessfullyCreated_ReturnsCreatedStatus() {
        Brand brand = new Brand("Starbucks");
        brand.setId(1L);
        when(brandService.createBrand(brand)).thenReturn(brand);

        var result = brandController.createBrand(brand);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void createBrand_WhenBrandSuccessfullyCreated_ReturnsCreatedBrand() {
        Brand brand = new Brand("Starbucks");
        brand.setId(1L);
        when(brandService.createBrand(brand)).thenReturn(brand);

        var result = brandController.createBrand(brand);

        assertSame(brand, result.getBody());
    }

    @Test
    void createBrand_WhenBrandServiceThrowsException_ReturnsInternalServerErrorStatus() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);
        when(brandService.createBrand(brand)).thenThrow(ServiceException.class);

        var result = brandController.createBrand(brand);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void createBrand_WhenBrandRequestedNoName_ReturnsBadRequestStatus() {
        var invalidBrand = new Brand();

        var result = brandController.createBrand(invalidBrand);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    //endregion

    //region GET
    // happy
    // get id that exists return brand
    // unhappy
    // get id that doesn't exist returns 404 not found
    // brand service throws exception returns 500

    @Test
    void getBrand_WhenBrandExists_ReturnsOKStatus() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);

        when(brandService.getBrand(brand.getId())).thenReturn(brand);

        var result = brandController.getBrand(brand.getId());

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getBrand_WhenBrandExists_ReturnsBrand() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);

        when(brandService.getBrand(brand.getId())).thenReturn(brand);

        var result = brandController.getBrand(brand.getId());

        assertEquals(brand, result.getBody());
    }

    @Test
    void getBrand_WhenBrandDoesNotExist_ReturnsNotFoundStatus() {
        when(brandService.getBrand(1L)).thenThrow(BrandNotFoundException.class);

        var result = brandController.getBrand(1L);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getBrand_WhenBrandServiceThrowsServiceException_ReturnsInternalServerErrorStatus() {
        when(brandService.getBrand(1L)).thenThrow(ServiceException.class);

        var result = brandController.getBrand(1L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
    // endregion

    // region GET ALL
    // happy
    // whenBrandsExist_ReturnsAllBrands
    // whenBrandsExist_Returns200
    // unhappy
    // whenNoBrandsExist_Return404
    // whenBrandServiceThrowsServiceException_Return500
    @Test
    void getAllBrands_WhenBrandsExist_ReturnsOkStatus() {
        var brand1 = new Brand("Starbucks");
        brand1.setId(1L);
        var brand2 = new Brand("Costa");
        brand2.setId(2L);

        var allBrands = new ArrayList<Brand>();
        allBrands.add(brand1);
        allBrands.add(brand2);

        when(brandService.getAllBrands()).thenReturn(allBrands);
        var result = brandController.getAllBrands();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getAllBrands_WhenBrandsExist_ReturnsAllBrands() {
        var brand1 = new Brand("Starbucks");
        brand1.setId(1L);
        var brand2 = new Brand("Costa");
        brand2.setId(2L);

        var allBrands = new ArrayList<Brand>();
        allBrands.add(brand1);
        allBrands.add(brand2);

        when(brandService.getAllBrands()).thenReturn(allBrands);
        var result = brandController.getAllBrands();

        assertEquals(allBrands, result.getBody());
    }

    @Test
    void getAllBrands_WhenBrandDoesNotExist_ReturnsNotFoundStatus() {
        when(brandService.getAllBrands()).thenThrow(BrandNotFoundException.class);

        var result = brandController.getAllBrands();
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getAllBrands_WhenBrandServiceThrowsServiceException_ReturnsInternalServerErrorStatus() {
        when(brandService.getAllBrands()).thenThrow(ServiceException.class);

        var result = brandController.getAllBrands();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
    // endregion

    // region DELETE
    // happy
    // whenBrandExists_IfDeleteIsSuccessful_Return204
    // unhappy
    // whenBrandServiceDoesn'tReturnID_Return??
    // whenBrandDoesntExist_Return404
    // whenBrandServiceThrowsServiceException_Return500

    @Test
    void deleteBrand_WhenBrandExists_ReturnsNoContentStatus() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);

        when(brandService.deleteBrand(brand.getId())).thenReturn(brand.getId());

        var result = brandController.deleteBrand(brand.getId());
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void deleteBrand_WhenBrandDoesNotExist_ReturnsNotFoundStatus() {
        when(brandService.deleteBrand(1L)).thenReturn(null);

        var result = brandController.deleteBrand(1L);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void deleteBrand_WhenBrandServiceThrowsServiceException_ReturnsInternalServerErrorStatus() {
        when(brandService.deleteBrand(1L)).thenThrow(ServiceException.class);

        var result = brandController.deleteBrand(1L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
    // endregion
}