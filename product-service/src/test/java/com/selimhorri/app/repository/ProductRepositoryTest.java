package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.domain.Category;
import com.selimhorri.app.domain.Product;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Test
    @DisplayName("Test save product")
    void testSaveProduct() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        Product product = Product.builder()
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
        
        // Act
        Product savedProduct = productRepository.save(product);
        
        // Assert
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getProductId());
        assertEquals("Smartphone", savedProduct.getProductTitle());
        assertEquals("PHONE123", savedProduct.getSku());
        assertEquals(999.99, savedProduct.getPriceUnit());
        assertEquals(10, savedProduct.getQuantity());
    }
    
    @Test
    @DisplayName("Test find product by ID")
    void testFindById() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        Product product = Product.builder()
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
        
        entityManager.persistAndFlush(product);
        
        // Act
        Optional<Product> foundProduct = productRepository.findById(product.getProductId());
        
        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product.getProductId(), foundProduct.get().getProductId());
        assertEquals("Smartphone", foundProduct.get().getProductTitle());
        assertEquals("PHONE123", foundProduct.get().getSku());
    }
    
    @Test
    @DisplayName("Test find all products")
    void testFindAll() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        Product product1 = Product.builder()
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
        
        Product product2 = Product.builder()
                .productTitle("Laptop")
                .imageUrl("http://example.com/laptop.jpg")
                .sku("LAPTOP456")
                .priceUnit(1499.99)
                .quantity(5)
                .category(category)
                .build();
        
        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        
        // Act
        List<Product> products = productRepository.findAll();
        
        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
    }
    
    @Test
    @DisplayName("Test delete product")
    void testDeleteProduct() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        Product product = Product.builder()
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
        
        entityManager.persistAndFlush(product);
        
        // Act
        productRepository.deleteById(product.getProductId());
        
        // Assert
        Optional<Product> deletedProduct = productRepository.findById(product.getProductId());
        assertTrue(deletedProduct.isEmpty());
    }
    
    @Test
    @DisplayName("Test update product")
    void testUpdateProduct() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        Product product = Product.builder()
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .category(category)
                .build();
        
        entityManager.persistAndFlush(product);
        
        // Act - Actualizar el producto
        product.setProductTitle("Updated Smartphone");
        product.setPriceUnit(899.99);
        Product updatedProduct = productRepository.save(product);
        
        // Assert
        assertNotNull(updatedProduct);
        assertEquals(product.getProductId(), updatedProduct.getProductId());
        assertEquals("Updated Smartphone", updatedProduct.getProductTitle());
        assertEquals(899.99, updatedProduct.getPriceUnit());
    }
}
