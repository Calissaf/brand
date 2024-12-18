package org.qrush.brand.brand;

import jakarta.validation.Valid;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.dto.BrandResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrand(@PathVariable UUID id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @GetMapping()
    public ResponseEntity<BrandResponse> getAllBrands(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        BrandResponse response = brandService.getAllBrands(pageNo, pageSize);

        if(response.getContent().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(brandService.getAllBrands(pageNo, pageSize));
    }

    @PostMapping()
    public ResponseEntity<BrandDto> createBrand(@RequestBody @Valid BrandDto brandDto) {
        return new ResponseEntity<>(brandService.createBrand(brandDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BrandDto> updateBrand(@PathVariable UUID id, @RequestBody @Valid BrandDto brandDto) {
        return new ResponseEntity<>(brandService.updateBrand(brandDto,id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
