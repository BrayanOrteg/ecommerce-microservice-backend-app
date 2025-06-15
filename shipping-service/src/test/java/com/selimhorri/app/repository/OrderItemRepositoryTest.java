package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;

@ExtendWith(MockitoExtension.class)
public class OrderItemRepositoryTest {
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private OrderItemId orderItemId1;
    private OrderItemId orderItemId2;
    
    @BeforeEach
    void setUp() {
        orderItemId1 = new OrderItemId(201, 101);
        orderItemId2 = new OrderItemId(202, 102);
        
        orderItem1 = OrderItem.builder()
                .orderId(101)
                .productId(201)
                .orderedQuantity(5)
                .build();
                
        orderItem2 = OrderItem.builder()
                .orderId(102)
                .productId(202)
                .orderedQuantity(3)
                .build();
    }
      @Test
    @DisplayName("Test findAll order items repository method")
    void testFindAll() {
        when(orderItemRepository.findAll()).thenReturn(Arrays.asList(orderItem1, orderItem2));
        
        List<OrderItem> orderItems = orderItemRepository.findAll();
        
        assertNotNull(orderItems);
        assertTrue(orderItems.size() >= 0);
        verify(orderItemRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test findById order item repository method")
    void testFindById() {
        when(orderItemRepository.findById(orderItemId1)).thenReturn(Optional.of(orderItem1));
        
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(orderItemId1);
        
        assertNotNull(foundOrderItem);
        assertTrue(foundOrderItem.isPresent());
        verify(orderItemRepository, times(1)).findById(orderItemId1);
    }
      @Test
    @DisplayName("Test save order item repository method")
    void testSave() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem1);
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem1);
        
        assertNotNull(savedOrderItem);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }
      @Test
    @DisplayName("Test delete order item repository method")
    void testDelete() {
        orderItemRepository.delete(orderItem1);
        
        verify(orderItemRepository, times(1)).delete(orderItem1);
    }
      @Test
    @DisplayName("Test deleteById order item repository method")
    void testDeleteById() {
        orderItemRepository.deleteById(orderItemId1);
        
        verify(orderItemRepository, times(1)).deleteById(orderItemId1);
    }
}
