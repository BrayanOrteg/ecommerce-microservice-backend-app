package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;
import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.exception.wrapper.ProductNotFoundException;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product product1;
    private Product product2;
    private ProductDto productDto1;
    private ProductDto productDto2;
    private Category category;
    private CategoryDto categoryDto;
    
    @BeforeEach
    void setUp() {
        // Configurar categoría
        category = Category.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
                
        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
                
        // Configurar productos
        product1 = Product.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
                
        product2 = Product.builder()
                .productId(2)
                .productTitle("Laptop")
                .imageUrl("http://example.com/laptop.jpg")
                .sku("LAPTOP456")
                .priceUnit(1499.99)
                .quantity(5)
                .category(category)
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
    }
    
    @Test
    @DisplayName("Test findAll products")
    void testFindAll() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
        
        // Act
        List<ProductDto> products = productService.findAll();
        
        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Smartphone", products.get(0).getProductTitle());
        assertEquals("Laptop", products.get(1).getProductTitle());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test findById with existing product")
    void testFindByIdWithExistingProduct() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        
        // Act
        ProductDto foundProduct = productService.findById(1);
        
        // Assert
        assertNotNull(foundProduct);
        assertEquals(1, foundProduct.getProductId());
        assertEquals("Smartphone", foundProduct.getProductTitle());
        assertEquals("PHONE123", foundProduct.getSku());
        verify(productRepository, times(1)).findById(1);
    }
    
    @Test
    @DisplayName("Test findById with non-existing product")
    void testFindByIdWithNonExistingProduct() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.findById(999);
        });
        verify(productRepository, times(1)).findById(999);
    }
    
    @Test
    @DisplayName("Test save product")
    void testSaveProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        
        // Act
        ProductDto savedProduct = productService.save(productDto1);
        
        // Assert
        assertNotNull(savedProduct);
        assertEquals(1, savedProduct.getProductId());
        assertEquals("Smartphone", savedProduct.getProductTitle());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test update product")
    void testUpdateProduct() {
        // Arrange
        ProductDto updatedProductDto = productDto1;
        updatedProductDto.setProductTitle("Updated Smartphone");
        updatedProductDto.setPriceUnit(899.99);
        
        Product updatedProduct = product1;
        updatedProduct.setProductTitle("Updated Smartphone");
        updatedProduct.setPriceUnit(899.99);
        
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        
        // Act
        ProductDto result = productService.update(updatedProductDto);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Smartphone", result.getProductTitle());
        assertEquals(899.99, result.getPriceUnit());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test update product with ID")
    void testUpdateProductWithId() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        
        // Act
        ProductDto result = productService.update(1, productDto1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getProductId());
        assertEquals("Smartphone", result.getProductTitle());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test delete product by ID")
    void testDeleteById() {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        doNothing().when(productRepository).delete(any(Product.class));
        
        // Act
        productService.deleteById(1);
        
        // Assert
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(any(Product.class));
    }
}
