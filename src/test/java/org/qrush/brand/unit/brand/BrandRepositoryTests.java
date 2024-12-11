package org.qrush.brand.unit.brand;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.qrush.brand.brand.BrandRepository;
import org.qrush.brand.brand.models.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void brandRepository_Save_ReturnsSavedBrand() {
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        Brand savedBrand = brandRepository.save(brand);

        assertNotNull(savedBrand);
        assertNotNull(savedBrand.getId());
        assertEquals(brand.getName(), savedBrand.getName());
    }

    @Test
    public void brandRepository_Save_GivenEmptyBrandName_ThrowsConstraintViolationException() {
        Brand brand = Brand.builder()
                .name("")
                .build();

        assertThrows(ConstraintViolationException.class, () -> brandRepository.saveAndFlush(brand));
    }

    @Test
    public void brandRepository_Save_GivenNullBrandName_ThrowsConstraintViolationException() {
        Brand brand = Brand.builder()
                .name(null)
                .build();

        assertThrows(ConstraintViolationException.class, () -> brandRepository.saveAndFlush(brand));
    }

    @Test
    public void brandRepository_SaveDuplicateBrand_ThrowsDataIntegrityViolationException() {
        Brand brand1 = Brand.builder()
                .name("Starbucks")
                .build();

        Brand brand2 = Brand.builder()
                .name("Starbucks")
                .build();

        brandRepository.save(brand1);
        assertThrows(DataIntegrityViolationException.class, () -> brandRepository.saveAndFlush(brand2));
    }

    @Test
    public void brandRepository_FindAll_ReturnsAllBrands() {
        Brand brand1 = Brand.builder()
                .name("Starbucks")
                .build();

        Brand brand2 = Brand.builder()
                .name("Costa")
                .build();

        brandRepository.save(brand1);
        brandRepository.save(brand2);

        List<Brand> brands = brandRepository.findAll();

        assertNotNull(brands);
        assertEquals(brands.size(), 2);
        assertTrue(brands.contains(brand1));
        assertTrue(brands.contains(brand2));
    }

    @Test
    public void brandRepository_FindById_ReturnsBrand() {
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        brandRepository.save(brand);

        Brand foundBrand = brandRepository.findById(brand.getId()).orElse(null);

        assertNotNull(foundBrand);
        assertNotNull(foundBrand.getId());
        assertEquals(brand.getName(), foundBrand.getName());
    }

    @Test
    public void brandRepository_FindById_GivenBrandIdDoesntExist_ReturnsNull() {
        Brand foundBrand = brandRepository.findById(UUID.randomUUID()).orElse(null);

        assertNull(foundBrand);
    }

    @Test
    public void brandRepository_FindByName_ReturnsBrand() {
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        brandRepository.save(brand);

        Brand foundBrand = brandRepository.findByName(brand.getName()).orElse(null);

        assertNotNull(foundBrand);
        assertNotNull(foundBrand.getId());
        assertEquals(brand.getName(), foundBrand.getName());
    }

    @Test
    public void brandRepository_FindByName_GivenBrandNameDoesntExist_ReturnsNull() {
        Brand foundBrand = brandRepository.findByName("Starbucks").orElse(null);

        assertNull(foundBrand);
    }

    @Test
    public void brandRepository_BrandDelete_ReturnsBrandIsEmpty() {
        Brand brand = Brand.builder()
                .name("Starbucks")
                .build();

        brandRepository.save(brand);

        brandRepository.delete(brand);

        Optional<Brand> foundBrand = brandRepository.findById(brand.getId());

        assertFalse(foundBrand.isPresent());
    }
}
