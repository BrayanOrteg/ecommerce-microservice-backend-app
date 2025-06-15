package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.selimhorri.app.domain.OrderItem;
import com.selimhorri.app.domain.id.OrderItemId;
import com.selimhorri.app.dto.OrderItemDto;
import com.selimhorri.app.repository.OrderItemRepository;
import com.selimhorri.app.service.impl.OrderItemServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private OrderItemServiceImpl orderItemService;
    
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private OrderItemDto orderItemDto1;
    private OrderItemId orderItemId1;
      @BeforeEach
    void setUp() {
        orderItemId1 = new OrderItemId(101, 1);
        
        orderItem1 = OrderItem.builder()
                .productId(101)
                .orderId(1)
                .orderedQuantity(2)
                .build();
                
        orderItem2 = OrderItem.builder()
                .productId(102)
                .orderId(2)
                .orderedQuantity(5)
                .build();
                
        orderItemDto1 = OrderItemDto.builder()
                .orderedQuantity(2)
                .build();
    }
      @Test
    @DisplayName("Test repository findAll interaction")
    void testFindAll() {
        when(orderItemRepository.findAll()).thenReturn(Arrays.asList(orderItem1, orderItem2));
        
        List<OrderItemDto> orderItems = orderItemService.findAll();
        
        assertNotNull(orderItems);
        verify(orderItemRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test repository save interaction")
    void testSaveOrderItem() {
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem1);
        
        OrderItemDto savedOrderItem = orderItemService.save(orderItemDto1);
        
        assertNotNull(savedOrderItem);
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }
      @Test
    @DisplayName("Test repository deleteById interaction")
    void testDeleteById() {
        doNothing().when(orderItemRepository).deleteById(any(OrderItemId.class));
        
        orderItemService.deleteById(orderItemId1);
        
        verify(orderItemRepository, times(1)).deleteById(orderItemId1);
    }
}
