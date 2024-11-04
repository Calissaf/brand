package org.qrush.brand.unit.brand;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.qrush.brand.brand.BrandRepository;
import org.qrush.brand.brand.BrandService;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.dto.BrandResponse;
import org.qrush.brand.brand.exceptions.BrandAlreadyExists;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.qrush.brand.brand.models.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceTests {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    //region CREATE
    @Test
    public void BrandService_CreateBrand_ReturnsBrandDto() {
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        BrandDto brandDto = BrandDto.builder()
                .name(brand.getName())
                .build();

        when(brandRepository.save(Mockito.any(Brand.class))).thenReturn(brand);

        BrandDto savedBrandDto = brandService.createBrand(brandDto);

        assertNotNull(savedBrandDto);
        assertEquals(savedBrandDto.getName(), brand.getName());
    }

    @Test
    public void BrandService_CreateBrand_WhenBrandAlreadyExists_ThrowBrandAlreadyExistsException() {
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        BrandDto brandDto = BrandDto.builder()
                .name(brand.getName())
                .build();

        when(brandRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.of(brand));

        assertThrows(BrandAlreadyExists.class, () -> brandService.createBrand(brandDto));
    }
    //endregion

    //region GET
    @Test
    void BrandService_FindById_ReturnsBrandDto() {
        Long id = 1L;

        Brand brand = Brand.builder()
                .id(id)
                .name("Starbucks")
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.ofNullable(brand));

        BrandDto brandReturn = brandService.getBrandById(id);

        assertNotNull(brandReturn);
        assertEquals(brand.getName(), brandReturn.getName());
        assertEquals(brand.getId(), brandReturn.getId());
    }

    @Test
    void BrandService_FindById_BrandDoesntExist_ThrowsBrandNotFoundException() {
        Long id = 1L;

        when(brandRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class, () -> brandService.getBrandById(id));
    }
    //endregion

    // region GET all brands
    @Test
    public void BrandService_FindAll_ReturnsBrandResponse() {
        @SuppressWarnings("unchecked")
        Page<Brand> brands = Mockito.mock(Page.class);

        when(brandRepository.findAll(Mockito.any(Pageable.class))).thenReturn(brands);

        BrandResponse brandResponse = brandService.getAllBrands(1, 10);

        assertNotNull(brandResponse);
    }
    //endregion

    //region DELETE
    @Test
    public void BrandService_DeleteBrand_ReturnsVoid() {
        Long id = 1L;
        Brand brand = Brand.builder()
                .name("Starbucks")
                .id(id)
                .build();

        when(brandRepository.findById(id)).thenReturn(Optional.ofNullable(brand));

        assertNotNull(brand);
        doNothing().when(brandRepository).delete(brand);

        assertAll(() -> brandService.deleteBrand(id));
    }

    @Test
    void BrandService_DeleteBrand_BrandDoesntExist_ThrowsBrandNotFoundException() {
        Long id = 1L;

        when(brandRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class, () -> brandService.deleteBrand(id));
    }
    //endregion
}