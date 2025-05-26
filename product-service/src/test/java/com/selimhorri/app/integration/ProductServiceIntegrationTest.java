package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:data-test.sql"}, 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:cleanup-test.sql"}, 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ProductServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String createUrl(String path) {
        return "http://localhost:" + port + "/api/products" + (path != null ? path : "");
    }

    @Test
    @DisplayName("Test integration - get all products")
    public void testGetAllProducts() {
        // Arrange
        String url = createUrl("");
        
        // Act
        HttpEntity<?> request = new HttpEntity<>(createJsonHeaders());
        ResponseEntity<DtoCollectionResponse<ProductDto>> response = restTemplate.exchange(
            url, 
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<DtoCollectionResponse<ProductDto>>() {}
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getCollection().size() > 0);
    }
    
    @Test
    @DisplayName("Test integration - get product by ID")
    public void testGetProductById() {
        // Arrange
        int productId = 1;
        String url = createUrl("/" + productId);
        
        // Act
        HttpEntity<?> request = new HttpEntity<>(createJsonHeaders());
        ResponseEntity<ProductDto> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            ProductDto.class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().getProductId());
    }
    
    @Test
    @DisplayName("Test integration - create new product")
    public void testCreateProduct() {
        // Arrange
        String url = createUrl("");
        
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
                
        ProductDto newProduct = ProductDto.builder()
                .productTitle("New Test Product")
                .imageUrl("http://example.com/test.jpg")
                .sku("TEST123")
                .priceUnit(99.99)
                .quantity(5)
                .categoryDto(categoryDto)
                .build();
                
        // Act
        HttpEntity<ProductDto> request = new HttpEntity<>(newProduct, createJsonHeaders());
        ResponseEntity<ProductDto> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            ProductDto.class
        );
          // Assert for create product
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getProductId());
        assertEquals("New Test Product", response.getBody().getProductTitle());
        assertEquals("TEST123", response.getBody().getSku());
    }
    
    @Test
    @DisplayName("Test integration - update product")
    public void testUpdateProduct() {
        // Arrange
        int productId = 1;
        String url = createUrl("");
        
        // Get the current product
        ResponseEntity<ProductDto> getResponse = restTemplate.exchange(
            createUrl("/" + productId),
            HttpMethod.GET,
            new HttpEntity<>(createJsonHeaders()),
            ProductDto.class
        );
        
        ProductDto existingProduct = getResponse.getBody();
        assertNotNull(existingProduct);
        
        // Modify the product
        existingProduct.setProductTitle("Updated Product Title");
        existingProduct.setPriceUnit(109.99);
        
        // Act - Update the product
        HttpEntity<ProductDto> request = new HttpEntity<>(existingProduct, createJsonHeaders());
        ResponseEntity<ProductDto> updateResponse = restTemplate.exchange(
            url,
            HttpMethod.PUT,
            request,
            ProductDto.class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        ProductDto updated = updateResponse.getBody();
        assertEquals(productId, updated.getProductId());
        assertEquals("Updated Product Title", updated.getProductTitle());
        assertEquals(109.99, updated.getPriceUnit());
    }
    
    @Test
    @DisplayName("Test integration - delete product")
    public void testDeleteProduct() {
        // Arrange - Create a product to delete
        String createUrl = createUrl("");
        
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
                
        ProductDto newProduct = ProductDto.builder()
                .productTitle("Product To Delete")
                .imageUrl("http://example.com/delete.jpg")
                .sku("DELETE123")
                .priceUnit(49.99)
                .quantity(2)
                .categoryDto(categoryDto)
                .build();
                
        // Create the product
        HttpEntity<ProductDto> createRequest = new HttpEntity<>(newProduct, createJsonHeaders());
        ResponseEntity<ProductDto> createResponse = restTemplate.exchange(
            createUrl,
            HttpMethod.POST,
            createRequest,
            ProductDto.class
        );
        assertNotNull(createResponse.getBody());
        Integer createdProductId = createResponse.getBody().getProductId();
        
        // Act - Delete the product
        String deleteUrl = createUrl("/" + createdProductId);        
        HttpEntity<?> deleteRequest = new HttpEntity<>(createJsonHeaders());
        
        // Skip boolean parsing entirely and just use Void.class for the response
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            deleteUrl, 
            HttpMethod.DELETE, 
            deleteRequest, 
            Void.class
        );
          // Assert - Verify deletion was successful by HTTP status only
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
          // Verify that the product no longer exists
        ResponseEntity<ProductDto> getDeletedResponse = restTemplate.exchange(
            deleteUrl,
            HttpMethod.GET,
            deleteRequest,
            ProductDto.class
        );
        
        // Should get 404 Not Found for non-existent product
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
    }
}
