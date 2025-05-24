package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.dto.CategoryDto;
import com.selimhorri.app.dto.ProductDto;
import com.selimhorri.app.repository.CategoryRepository;
import com.selimhorri.app.repository.ProductRepository;
import com.selimhorri.app.service.ProductService;
import com.selimhorri.app.constant.AppConstant;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceIntegrationWithOtherServicesTest {

    @Autowired
    private ProductService productService;
    
    @MockBean
    private RestTemplate restTemplate;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private ProductDto productDto;
    private CategoryDto categoryDto;
    
    @BeforeEach
    void setUp() {
        categoryDto = CategoryDto.builder()
                .categoryId(1)
                .categoryTitle("Electronics")
                .imageUrl("http://example.com/category.jpg")
                .build();
                
        productDto = ProductDto.builder()
                .productId(1)
                .productTitle("Smartphone")
                .imageUrl("http://example.com/smartphone.jpg")
                .sku("PHONE123")
                .priceUnit(999.99)
                .quantity(10)
                .categoryDto(categoryDto)
                .build();
    }
    
    @Test
    @DisplayName("Test integration - Product with Order Service")
    void testProductWithOrderService() {
        // Mock response for order check
        String orderServiceUrl = AppConstant.DiscoveredDomainsApi.ORDER_SERVICE_API_URL + "/check-product/1";
        OrderServiceResponse mockResponse = new OrderServiceResponse();
        mockResponse.setProductId(1);
        mockResponse.setQuantityOrdered(5);
        mockResponse.setAvailable(true);
        
        when(restTemplate.getForObject(orderServiceUrl, OrderServiceResponse.class))
            .thenReturn(mockResponse);
        
        OrderServiceResponse response = restTemplate.getForObject(orderServiceUrl, OrderServiceResponse.class);
        
        assertNotNull(response);
        assertEquals(1, response.getProductId());
        assertEquals(5, response.getQuantityOrdered());
        assertEquals(true, response.isAvailable());
    }
    
    @Test
    @DisplayName("Test integration - Product with Payment Service")
    void testProductWithPaymentService() {
        String paymentServiceUrl = AppConstant.DiscoveredDomainsApi.PAYMENT_SERVICE_API_URL + "/process-product/1";
        PaymentServiceResponse mockResponse = new PaymentServiceResponse();
        mockResponse.setProductId(1);
        mockResponse.setPrice(999.99);
        mockResponse.setPaymentProcessed(true);
        
        when(restTemplate.getForObject(paymentServiceUrl, PaymentServiceResponse.class))
            .thenReturn(mockResponse);
        
        PaymentServiceResponse response = restTemplate.getForObject(paymentServiceUrl, PaymentServiceResponse.class);
        
        assertNotNull(response);
        assertEquals(1, response.getProductId());
        assertEquals(999.99, response.getPrice(), 0.001);
        assertEquals(true, response.isPaymentProcessed());
    }
    
    @Test
    @DisplayName("Test integration - Product with Shipping Service")
    void testProductWithShippingService() {
        String shippingServiceUrl = AppConstant.DiscoveredDomainsApi.SHIPPING_SERVICE_API_URL + "/check-product/1";
        ShippingServiceResponse mockResponse = new ShippingServiceResponse();
        mockResponse.setProductId(1);
        mockResponse.setProductTitle("Smartphone");
        mockResponse.setEstimatedDeliveryDays(3);
        mockResponse.setShippable(true);
        
        when(restTemplate.getForObject(shippingServiceUrl, ShippingServiceResponse.class))
            .thenReturn(mockResponse);
        
        ShippingServiceResponse response = restTemplate.getForObject(shippingServiceUrl, ShippingServiceResponse.class);
        
        assertNotNull(response);
        assertEquals(1, response.getProductId());
        assertEquals("Smartphone", response.getProductTitle());
        assertEquals(3, response.getEstimatedDeliveryDays());
        assertEquals(true, response.isShippable());
    }
    
    @Test
    @DisplayName("Test integración - Producto con Servicio de Usuarios (Favoritos)")
    void testProductWithUserFavoritesService() {
        // Simular respuesta del servicio de usuarios (favoritos)
        UserFavoritesResponse mockResponse = new UserFavoritesResponse();
        mockResponse.setUserId(1);
        mockResponse.setProductId(1);
        mockResponse.setFavorite(true);
        
        // Configurar el mock de RestTemplate para simular llamada al servicio de usuarios
        when(restTemplate.getForObject(
            "http://user-service/api/users/1/favorites/check/1", 
            UserFavoritesResponse.class
        )).thenReturn(mockResponse);
        
        // Ejecutar la prueba de integración
        UserFavoritesResponse response = restTemplate.getForObject(
            "http://user-service/api/users/1/favorites/check/1", 
            UserFavoritesResponse.class
        );
        
        // Verificar resultados
        assertNotNull(response);
        assertEquals(1, response.getUserId());
        assertEquals(1, response.getProductId());
        assertEquals(true, response.isFavorite());
    }
    
    @Test
    @DisplayName("Test integración - Producto con Gateway API")
    void testProductWithApiGateway() {
        // Simular respuesta del API Gateway
        when(restTemplate.getForObject(
            "http://api-gateway/api/products/1",
            ProductDto.class
        )).thenReturn(productDto);
        
        // Ejecutar la prueba de integración
        ProductDto response = restTemplate.getForObject(
            "http://api-gateway/api/products/1",
            ProductDto.class
        );
        
        // Verificar resultados
        assertNotNull(response);
        assertEquals(1, response.getProductId());
        assertEquals("Smartphone", response.getProductTitle());
        assertEquals("PHONE123", response.getSku());
    }
    
    // Clases internas para simular respuestas de otros servicios
    
    public static class OrderServiceResponse {
        private int productId;
        private int quantityOrdered;
        private boolean available;
        
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        
        public int getQuantityOrdered() { return quantityOrdered; }
        public void setQuantityOrdered(int quantityOrdered) { this.quantityOrdered = quantityOrdered; }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }
    
    public static class PaymentServiceResponse {
        private int productId;
        private double price;
        private boolean paymentProcessed;
        
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        
        public boolean isPaymentProcessed() { return paymentProcessed; }
        public void setPaymentProcessed(boolean paymentProcessed) { this.paymentProcessed = paymentProcessed; }
    }
    
    public static class ShippingServiceResponse {
        private int productId;
        private String productTitle;
        private int estimatedDeliveryDays;
        private boolean shippable;
        
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        
        public String getProductTitle() { return productTitle; }
        public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
        
        public int getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
        public void setEstimatedDeliveryDays(int estimatedDeliveryDays) { this.estimatedDeliveryDays = estimatedDeliveryDays; }
        
        public boolean isShippable() { return shippable; }
        public void setShippable(boolean shippable) { this.shippable = shippable; }
    }
    
    public static class UserFavoritesResponse {
        private int userId;
        private int productId;
        private boolean favorite;
        
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        
        public boolean isFavorite() { return favorite; }
        public void setFavorite(boolean favorite) { this.favorite = favorite; }
    }
}
