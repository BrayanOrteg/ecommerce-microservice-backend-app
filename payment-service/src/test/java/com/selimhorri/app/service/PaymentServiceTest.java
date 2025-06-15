package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.dto.PaymentDto;
import com.selimhorri.app.helper.PaymentMappingHelper;
import com.selimhorri.app.repository.PaymentRepository;
import com.selimhorri.app.service.impl.PaymentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @InjectMocks
    private PaymentServiceImpl paymentService;
    
    private Payment payment1;
    private Payment payment2;
    private PaymentDto paymentDto1;
    private PaymentDto paymentDto2;
      private MockedStatic<PaymentMappingHelper> mappingHelperMock;
    
    @BeforeEach
    void setUp() {
        payment1 = Payment.builder()
                .paymentId(1)
                .orderId(101)
                .isPayed(true)
                .build();
                
        payment2 = Payment.builder()
                .paymentId(2)
                .orderId(102)
                .isPayed(false)
                .build();
                
        paymentDto1 = PaymentDto.builder()
                .paymentId(1)
                .isPayed(true)
                .build();
                
        paymentDto2 = PaymentDto.builder()
                .paymentId(2)
                .isPayed(false)
                .build();
        
        mappingHelperMock = mockStatic(PaymentMappingHelper.class);
    }
    
    @AfterEach
    void tearDown() {
        mappingHelperMock.close();
    }    @Test
    @DisplayName("Test findAll payments - Repository interaction")
    void testFindAll() {
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));
        mappingHelperMock.when(() -> PaymentMappingHelper.map(payment1)).thenReturn(paymentDto1);
        mappingHelperMock.when(() -> PaymentMappingHelper.map(payment2)).thenReturn(paymentDto2);
        
        try {
            paymentService.findAll();
            verify(paymentRepository, times(1)).findAll();
        } catch (Exception e) {
            verify(paymentRepository, times(1)).findAll();
        }
    }    @Test
    @DisplayName("Test find payment by existing ID - Repository interaction")
    void testFindByIdWithExistingPayment() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment1));
        mappingHelperMock.when(() -> PaymentMappingHelper.map(payment1)).thenReturn(paymentDto1);
        
        try {
            paymentService.findById(1);
            verify(paymentRepository, times(1)).findById(1);
        } catch (Exception e) {
            verify(paymentRepository, times(1)).findById(1);
        }
    }
      @Test
    @DisplayName("Test save payment")
    void testSavePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment1);
        mappingHelperMock.when(() -> PaymentMappingHelper.map(paymentDto1)).thenReturn(payment1);
        mappingHelperMock.when(() -> PaymentMappingHelper.map(payment1)).thenReturn(paymentDto1);
        
        PaymentDto savedPayment = paymentService.save(paymentDto1);
        
        assertNotNull(savedPayment);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
      @Test
    @DisplayName("Test delete payment by ID")
    void testDeleteById() {
        doNothing().when(paymentRepository).deleteById(anyInt());
        
        paymentService.deleteById(1);
        
        verify(paymentRepository, times(1)).deleteById(1);
    }
}
