package org.qrush.brand.brand;

import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrandServiceTests {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //region GET
    //GET brand
    //happy
    //when valid id provided and brand exists return brand
    //unhappy
    //when brand does not exist throw service exception

    @Test
    void getBrand_WhenBrandExists_ReturnsBrand() {
        var brand = new Brand("Starbucks");
        var brandId = 1L;
        brand.setId(brandId);
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        var result = brandService.getBrand(brandId);

       assertSame(brand, result);
    }

    @Test
    void getBrand_WhenBrandDoesNotExist_ThrowsServiceException() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());
        var expectedMessage = "Brand not found";

        var exception = assertThrows(BrandNotFoundException.class, () -> brandService.getBrand(1L));
        assertEquals(expectedMessage, exception.getMessage());
    }


    // region GET all brands
    //happy
    //when x returns all brands
    //unhappy
    // when x throws service exception
    // ToDo: REWORK test to be more concise!!!
    @Test
    void getAllBrands_shouldReturnListOfBrands_whenBrandsExist() {

        // Arrange
        List<Brand> brands = Arrays.asList(
                new Brand("Starbucks"),
                new Brand("Costa")
        );
        brands.get(0).setId(1L);
        brands.get(1).setId(2L);

        when(brandRepository.findAll()).thenReturn(brands);

        // Act
        List<Brand> result = brandService.getAllBrands();

        // Assert
        assertSame(brands, result);
        verify(brandRepository, times(1)).findAll(); // ToDo: separate test?
    }

    @Test
    void getAllBrands_shouldThrowServiceException_whenBrandRepositoryThrowsException() {

        // Arrange
        when(brandRepository.findAll()).thenThrow(RuntimeException.class);
        var expectedMessage = "Brands not found";

        // Assert
        var exception = assertThrows(ServiceException.class, () -> brandService.getAllBrands());
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
    //endregion

    //region CREATE
    //CREATE brand
    //happy
    //create new brand
    //unhappy
    //when repository throws Service Exception throw ServiceException
    //if brand name null or empty throw ServiceException

    @Test
    void createBrand_shouldCreateBrand_whenBrandValid() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);
        when(brandRepository.save(brand)).thenReturn(brand);

        var result = brandRepository.save(brand);

        assertSame(brand, result);
    }

    @Test
    void createBrand_shouldThrowServiceException_whenBrandNameNull() {
        var brand = new Brand(null);
        brand.setId(1L);

        var expectedInternalMessage = "Brand name cannot be null or empty.";
        var expectedMessage = "Error creating brand";

        var exception = assertThrows(ServiceException.class, () -> brandService.createBrand(brand));
        var actualMessage = exception.getMessage();
        var actualInternalMessage = exception.getCause().getMessage();

        assertEquals(expectedInternalMessage, actualInternalMessage);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createBrand_shouldThrowServiceException_whenBrandNameEmpty() {
        var brand = new Brand("");
        brand.setId(1L);

        var expectedInternalMessage = "Brand name cannot be null or empty.";
        var expectedMessage = "Error creating brand";

        var exception = assertThrows(ServiceException.class, () -> brandService.createBrand(brand));
        var actualMessage = exception.getMessage();
        var actualInternalMessage = exception.getCause().getMessage();

        assertEquals(expectedInternalMessage, actualInternalMessage);
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createBrand_shouldThrowServiceException_whenBrandRepositoryThrowsException() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);

        when(brandRepository.save(brand)).thenThrow(RuntimeException.class);

        var expectedMessage = "Error creating brand";

        var exception = assertThrows(ServiceException.class, () -> brandService.createBrand(brand));
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void createBrand_shouldThrowServiceException_whenBrandRepositoryThrowsDataIntegrityException() {
        var brand = new Brand("Starbucks");
        brand.setId(1L);

        when(brandRepository.save(brand)).thenThrow(DataIntegrityViolationException.class);

        var expectedMessage = "Error saving brand: possible constraint violation.";

        var exception = assertThrows(ServiceException.class, () -> brandService.createBrand(brand));
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
    //endregion

    //region DELETE
    //DELETE brand
    //happy
    //when id exists delete brand
    // when id exists returns id
    //unhappy
    //when id does not exist return null
    //when repository throws exception throw service exception
    @Test
    void deleteBrand_WhenBrandExists_DeletesBrand() {
        when(brandRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> brandService.deleteBrand(1L));
        verify(brandRepository, times(1)).deleteById(1L);
    }
    @Test
    void deleteBrand_WhenBrandExists_ReturnsId() {
        var id = 1L;
        when(brandRepository.existsById(id)).thenReturn(true);

        var result = brandService.deleteBrand(id);
        assertEquals(id, result);
    }

    @Test
    void deleteBrand_WhenBrandDoesNotExist_ReturnNull() {
        when(brandRepository.existsById(1L)).thenReturn(false);

        var result = brandService.deleteBrand(1L);

        assertNull(result);
    }

    @Test
    void deleteBrand_WhenBrandRepositoryThrowsException_ThrowsServiceException() {
        when(brandRepository.existsById(1L)).thenReturn(true);
        doThrow(RuntimeException.class).when(brandRepository).deleteById(1L);
        var expectedMessage = "Error deleting brand";

        var exception = assertThrows(ServiceException.class, () -> brandService.deleteBrand(1L));
        var actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }


    //endregion
}