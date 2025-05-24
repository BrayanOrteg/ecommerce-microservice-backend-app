package com.selimhorri.app.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.service.ProductService;

@WebMvcTest(ProductResource.class)
public class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ProductService productService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private ProductDto productDto1;
    private ProductDto productDto2;
    private List<ProductDto> productDtos;
    
    @BeforeEach
    void setUp() {
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
                
        productDto1 = ProductDto.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .categoryDto(categoryDto)
                .build();
                
        productDto2 = ProductDto.builder()
                .productId(2)
                .productTitle("Laptop")
                .imageUrl("http://example.com/laptop.jpg")
                .sku("LAPTOP456")
                .priceUnit(1499.99)
                .quantity(5)
                .categoryDto(categoryDto)
                .build();
                
        productDtos = Arrays.asList(productDto1, productDto2);
    }
    
    @Test
    @DisplayName("Test GET all products")
    void testFindAll() throws Exception {
        // Arrange
        when(productService.findAll()).thenReturn(productDtos);
        
        // Act & Assert
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collection.size()", is(2)))
                .andExpect(jsonPath("$.collection[0].productId", is(1)))
                .andExpect(jsonPath("$.collection[0].productTitle", is("Smartphone")))
                .andExpect(jsonPath("$.collection[1].productId", is(2)))
                .andExpect(jsonPath("$.collection[1].productTitle", is("Laptop")));
    }
    
    @Test
    @DisplayName("Test GET product by ID")
    void testFindById() throws Exception {
        // Arrange
        when(productService.findById(1)).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productTitle", is("Smartphone")))
                .andExpect(jsonPath("$.sku", is("PHONE123")))
                .andExpect(jsonPath("$.priceUnit", is(999.99)))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.category.categoryId", is(1)))
                .andExpect(jsonPath("$.category.categoryTitle", is("Electronics")));
    }
    
    @Test
    @DisplayName("Test POST new product")
    void testSave() throws Exception {
        // Arrange
        when(productService.save(any(ProductDto.class))).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productTitle", is("Smartphone")))
                .andExpect(jsonPath("$.sku", is("PHONE123")));
    }
    
    @Test
    @DisplayName("Test PUT update product")
    void testUpdate() throws Exception {
        // Arrange
        when(productService.update(any(ProductDto.class))).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productTitle", is("Smartphone")));
    }
    
    @Test
    @DisplayName("Test PUT update product by ID")
    void testUpdateById() throws Exception {
        // Arrange
        when(productService.update(anyInt(), any(ProductDto.class))).thenReturn(productDto1);
        
        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productTitle", is("Smartphone")));
    }
      @Test
    @DisplayName("Test DELETE product")
    void testDelete() throws Exception {
        // Arrange
        doNothing().when(productService).deleteById(1);
        
        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
