package org.qrush.brand.brand;

import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.dto.BrandResponse;
import org.qrush.brand.brand.exceptions.BrandAlreadyExists;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.qrush.brand.brand.models.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public BrandDto getBrandById(UUID id) {
       Brand brand = brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException("Brand could not be found"));
       return mapToDto(brand);
    }

    public BrandResponse getAllBrands(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Brand> brands = brandRepository.findAll(pageable);
        List<Brand> listOfBrands = brands.getContent();
        List<BrandDto> content = listOfBrands.stream().map(this::mapToDto).toList();

        BrandResponse brandResponse = new BrandResponse();
        brandResponse.setContent(content);
        brandResponse.setPageNumber(brands.getNumber());
        brandResponse.setPageSize(brands.getSize());
        brandResponse.setTotalElements(brands.getTotalElements());
        brandResponse.setTotalPages(brands.getTotalPages());
        brandResponse.setLast(brands.isLast());

        return brandResponse;
    }

    public BrandDto createBrand(BrandDto brandDto) {
        if (checkBrandNameExists(brandDto)) {
            throw new BrandAlreadyExists("Brand name already exists");
        }

        Brand brand = new Brand();
        brand.setName(brandDto.getName());

        Brand newBrand = brandRepository.save(brand);

        BrandDto brandResponse = new BrandDto();
        brandResponse.setId(newBrand.getId());
        brandResponse.setName(newBrand.getName());
        return brandResponse;
    }

    public BrandDto updateBrand(BrandDto brandDto, UUID id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException("Brand not found"));

        if (checkBrandNameExists(brandDto)) {
            throw new BrandAlreadyExists("Brand name already exists");
        }

        brand.setName(brandDto.getName());

        Brand updatedBrand = brandRepository.save(brand);
        return mapToDto(updatedBrand);
    }

    public void deleteBrand(UUID id) {
       Brand brand = brandRepository.findById(id).orElseThrow(() -> new BrandNotFoundException("Brand not found"));
       brandRepository.delete(brand);
    }

    private BrandDto mapToDto(Brand brand) {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(brand.getId());
        brandDto.setName(brand.getName());
        return brandDto;
    }

    private boolean checkBrandNameExists(BrandDto brandDto) {
        var existingBrand = brandRepository.findByName(brandDto.getName()).orElse(null);
        return existingBrand != null;
    }
}

