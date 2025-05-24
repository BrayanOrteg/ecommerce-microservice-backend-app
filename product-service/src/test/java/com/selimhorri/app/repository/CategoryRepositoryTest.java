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

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Test
    @DisplayName("Test save category")
    void testSaveCategory() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        // Act
        Category savedCategory = categoryRepository.save(category);
        
        // Assert
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Electronics", savedCategory.getCategoryTitle());
        assertEquals("http://example.com/category.jpg", savedCategory.getImageUrl());
    }
    
    @Test
    @DisplayName("Test find category by ID")
    void testFindById() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        // Act
        Optional<Category> foundCategory = categoryRepository.findById(category.getCategoryId());
        
        // Assert
        assertTrue(foundCategory.isPresent());
        assertEquals(category.getCategoryId(), foundCategory.get().getCategoryId());
        assertEquals("Electronics", foundCategory.get().getCategoryTitle());
        assertEquals("http://example.com/category.jpg", foundCategory.get().getImageUrl());
    }
    
    @Test
    @DisplayName("Test find all categories")
    void testFindAll() {
        // Arrange
        Category category1 = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/electronics.jpg")
                .build();
        
        Category category2 = Category.builder()
                .categoryTitle("Clothing")
                .imageUrl("http://example.com/clothing.jpg")
                .build();
        
        Category category3 = Category.builder()
                .categoryTitle("Books")
                .imageUrl("http://example.com/books.jpg")
                .build();
        
        entityManager.persistAndFlush(category1);
        entityManager.persistAndFlush(category2);
        entityManager.persistAndFlush(category3);
        
        // Act
        List<Category> categories = categoryRepository.findAll();
        
        // Assert
        assertNotNull(categories);
        assertEquals(3, categories.size());
    }
    
    @Test
    @DisplayName("Test update category")
    void testUpdateCategory() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        // Act - Actualizar la categoría
        category.setCategoryTitle("Updated Electronics");
        category.setImageUrl("http://example.com/updated-electronics.jpg");
        Category updatedCategory = categoryRepository.save(category);
        
        // Assert
        assertNotNull(updatedCategory);
        assertEquals(category.getCategoryId(), updatedCategory.getCategoryId());
        assertEquals("Updated Electronics", updatedCategory.getCategoryTitle());
        assertEquals("http://example.com/updated-electronics.jpg", updatedCategory.getImageUrl());
    }
    
    @Test
    @DisplayName("Test delete category")
    void testDeleteCategory() {
        // Arrange
        Category category = Category.builder()
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
        
        entityManager.persistAndFlush(category);
        
        // Act
        categoryRepository.deleteById(category.getCategoryId());
        
        // Assert
        Optional<Category> deletedCategory = categoryRepository.findById(category.getCategoryId());
        assertTrue(deletedCategory.isEmpty());
    }
}
