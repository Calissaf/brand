package org.qrush.brand.brand;

import org.hibernate.service.spi.ServiceException;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/brand")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello biscuit %s!", name);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createBrand(@RequestBody Brand brand) {
        try {
            var result = brandService.createBrand(brand);
            var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.getId()).toUri();

            return ResponseEntity.created(location).body(result);
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Brand> getBrand(@RequestParam(value = "id") Long id) {
        try {
            var result = brandService.getBrand(id);
            return ResponseEntity.ok(result);
        } catch (BrandNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Brand>> getAllBrands() {
        try {
            var brands = brandService.getAllBrands();
            return ResponseEntity.ok(brands);
        } catch (BrandNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteBrand(@RequestParam(value = "id") Long id) {
        try {
            var result = brandService.deleteBrand(id);
            if (Objects.equals(result, id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
