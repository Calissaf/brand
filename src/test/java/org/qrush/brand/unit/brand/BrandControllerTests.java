package org.qrush.brand.unit.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.service.spi.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.qrush.brand.brand.BrandController;
import org.qrush.brand.brand.BrandService;
import org.qrush.brand.brand.dto.BrandDto;
import org.qrush.brand.brand.dto.BrandResponse;
import org.qrush.brand.brand.exceptions.BrandAlreadyExists;
import org.qrush.brand.brand.exceptions.BrandNotFoundException;
import org.qrush.brand.brand.models.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = BrandController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class BrandControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @Autowired
    private ObjectMapper objectMapper;
    private Brand brand;
    private BrandDto brandDto;

    @BeforeEach
    public void init() {
        brand = Brand.builder().name("Starbucks").build();
        brandDto = BrandDto.builder().name("Starbucks").build();
    }

    //region CREATE
    @Test
    void brandController_CreateBrand_ReturnsCreatedBrand() throws Exception {
        given(brandService.createBrand(ArgumentMatchers.any())).willAnswer((invocation -> invocation.getArgument(0)));

        ResultActions response = mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated());
        response.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(brandDto)));
    }

    @Test
    void brandController_CreateBrand_GivenEmptyJSON_ReturnsBadRequest() throws Exception {

        ResultActions response = mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.EMPTY_MAP)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.name").value("Brand name cannot be null or empty"));
    }

    @Test
    void brandController_CreateBrand_GivenInvalidJSON_ReturnsBadRequest() throws Exception {

        ResultActions response = mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonMap("request", "this doesn't match our schema"))));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.name").value("Brand name cannot be null or empty"));    }

    @Test
    void brandController_CreateBrand_WhenBrandServiceFails_ReturnInternalServerError() throws Exception {
        when(brandService.createBrand(ArgumentMatchers.any())).thenThrow(ServiceException.class);

        ResultActions response = mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void brandController_CreateBrand_WhenBrandAlreadyExists_ReturnsConflict() throws Exception {
        when(brandService.createBrand(ArgumentMatchers.any())).thenThrow(BrandAlreadyExists.class);

        ResultActions response = mockMvc.perform(post("/brand")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    //endregion

    //region GETBYID
    @Test
    void brandController_GetBrandById_ReturnsBrandDto() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.getBrandById(id)).thenReturn(brandDto);

        ResultActions response = mockMvc.perform(get("/brand/" + id));

        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(brandDto)));
    }

    @Test
    void brandController_GetBrandById_WhenBrandNotFound_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.getBrandById(id)).thenThrow(BrandNotFoundException.class);

        ResultActions response = mockMvc.perform(get("/brand/" + id));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void brandController_GetBrandById_WhenBrandServiceFails_ReturnsInternalServerError() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.getBrandById(id)).thenThrow(ServiceException.class);

        ResultActions response = mockMvc.perform(get("/brand/" + id));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
    //endregion

    //region GETALL
    @Test
    void brandController_GetAllBrands_ReturnsResponseDto() throws Exception {
        BrandResponse responseDto = BrandResponse.builder().pageNumber(0).pageSize(10).content(Collections.singletonList(brandDto)).build();
        when(brandService.getAllBrands(0, 10)).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/brand")
                .param("pageNo", "0")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseDto)));
    }

    @Test
    void brandController_GetAllBrands_WhenBrandServiceFails_ReturnsInternalServerError() throws Exception {
        when(brandService.getAllBrands(0, 10)).thenThrow(ServiceException.class);

        ResultActions response = mockMvc.perform(get("/brand")
                .param("pageNo", "0")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    void brandController_GetAllBrands_WhenBrandsNotFound_ReturnsNoContent() throws Exception {
        BrandResponse responseDto = BrandResponse.builder().pageNumber(0).pageSize(10).content(List.of()).build();
        when(brandService.getAllBrands(0, 10)).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/brand")
                .param("pageNo", "0")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }
    //endregion

    //region PUT
    @Test
    void brandController_UpdateBrand_ReturnsUpdatedBrand() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.updateBrand(brandDto, id)).thenReturn(brandDto);

        ResultActions response = mockMvc.perform(put("/brand/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
        response.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(brandDto)));
    }

    @Test
    void brandController_UpdateBrand_WhenBrandNotFound_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.updateBrand(brandDto, id)).thenThrow(BrandNotFoundException.class);

        ResultActions response = mockMvc.perform(put("/brand/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void brandController_UpdateBrand_WhenBrandNameAlreadyExists_ReturnsConflict() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.updateBrand(brandDto, id)).thenThrow(BrandAlreadyExists.class);

        ResultActions response = mockMvc.perform(put("/brand/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void brandController_UpdateBrand_GivenEmptyJSON_ReturnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();

        ResultActions response = mockMvc.perform(put("/brand/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.EMPTY_MAP)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.name").value("Brand name cannot be null or empty"));
    }

    @Test
    void brandController_UpdateBrand_GivenInvalidJSON_ReturnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();

        ResultActions response = mockMvc.perform(put("/brand/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonMap("request", "this doesn't match our schema"))));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        response.andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request parameters"));
        response.andExpect(MockMvcResultMatchers.jsonPath("$.invalid-params.name").value("Brand name cannot be null or empty"));    }

    @Test
    void brandController_UpdateBrand_WhenBrandServiceFails_ReturnInternalServerError() throws Exception {
        UUID id = UUID.randomUUID();
        when(brandService.updateBrand(brandDto, id)).thenThrow(ServiceException.class);

        ResultActions response = mockMvc.perform(put("/brand/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(brandDto)));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
    //endregion

    //region DELETE
    @Test
    void brandController_DeleteBrandById_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(brandService).deleteBrand(id);

        ResultActions response = mockMvc.perform(delete(String.format("/brand/%s/delete", id)));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void brandController_DeleteBrandById_WhenBrandNotFound_ReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(BrandNotFoundException.class).when(brandService).deleteBrand(id);

        ResultActions response = mockMvc.perform(delete(String.format("/brand/%s/delete", id)));

        response.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void brandController_DeleteBrandById_WhenBrandServiceFails_ReturnsInternalServerError() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(ServiceException.class).when(brandService).deleteBrand(id);

        ResultActions response = mockMvc.perform(delete(String.format("/brand/%s/delete", id)));

        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
    //endregion
}