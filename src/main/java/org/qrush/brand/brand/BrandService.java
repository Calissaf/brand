package org.qrush.brand.brand;

import org.hibernate.service.spi.ServiceException;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.qrush.brand.brand.exceptions.InvalidBrandException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Brand getBrand(Long id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isEmpty()) {
            throw new BrandNotFoundException("Brand not found");
        }
        return brand.get();
    }

    public List<Brand> getAllBrands() {
        try {
            var brands =  brandRepository.findAll();
            if (brands.isEmpty()) {
                throw new BrandNotFoundException("Brands not found");
            }
            return brands;
        } catch (Exception e) {
            throw new ServiceException("Brands not found", e);
        }
    }

    public Brand createBrand(Brand brand) {
        try {
           validateBrand(brand);
           return brandRepository.save(brand);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("Error saving brand: possible constraint violation.", e);
        } catch (Exception e) {
            throw new ServiceException("Error creating brand", e);
        }
    }

    public Long deleteBrand(Long id) {
        try {
            if (brandRepository.existsById(id)) {
                brandRepository.deleteById(id);
                return id;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ServiceException("Error deleting brand", e);
        }
    }

    private void validateBrand(Brand brand) {
        if (brand.getName() == null || brand.getName().isEmpty()) {
            throw new InvalidBrandException("Brand name cannot be null or empty.");
        }
    }
}

