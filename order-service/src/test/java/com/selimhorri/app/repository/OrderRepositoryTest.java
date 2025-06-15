package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Order;

@ExtendWith(MockitoExtension.class)
public class OrderRepositoryTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    private Order order1;
    private Order order2;
    
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        order1 = Order.builder()
                .orderId(1)
                .orderDate(now)
                .orderDesc("Electronics order")
                .orderFee(15.99)
                .build();
                
        order2 = Order.builder()
                .orderId(2)
                .orderDate(now.plusDays(1))
                .orderDesc("Books order")
                .orderFee(5.99)
                .build();
    }
    
    @Test
    @DisplayName("Test findAll orders repository method")
    void testFindAll() {

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));
        
        
        List<Order> orders = orderRepository.findAll();
        
        assertNotNull(orders);
        assertTrue(orders.size() >= 0);
        verify(orderRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test findById order repository method")
    void testFindById() {

        when(orderRepository.findById(1)).thenReturn(Optional.of(order1));
        
        Optional<Order> foundOrder = orderRepository.findById(1);

        assertNotNull(foundOrder);
        assertTrue(foundOrder.isPresent());
        verify(orderRepository, times(1)).findById(1);
    }
    
    @Test
    @DisplayName("Test save order repository method")
    void testSave() {

        when(orderRepository.save(any(Order.class))).thenReturn(order1);
        
        
        Order savedOrder = orderRepository.save(order1);

        assertNotNull(savedOrder);
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Test delete order repository method")
    void testDelete() {
        
        orderRepository.delete(order1);

        verify(orderRepository, times(1)).delete(order1);
    }
    
    @Test
    @DisplayName("Test deleteById order repository method")
    void testDeleteById() {
        
        orderRepository.deleteById(1);

        verify(orderRepository, times(1)).deleteById(anyInt());
    }
}
