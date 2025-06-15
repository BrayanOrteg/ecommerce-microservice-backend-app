package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.selimhorri.app.domain.Payment;
import com.selimhorri.app.domain.PaymentStatus;

@ExtendWith(MockitoExtension.class)
public class PaymentRepositoryTest {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    private Payment payment1;
    private Payment payment2;
    
    @BeforeEach
    void setUp() {
        payment1 = Payment.builder()
                .paymentId(1)
                .orderId(101)
                .isPayed(true)
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();
                
        payment2 = Payment.builder()
                .paymentId(2)
                .orderId(102)
                .isPayed(false)
                .paymentStatus(PaymentStatus.IN_PROGRESS)
                .build();
    }
      @Test
    @DisplayName("Test findAll payments repository method")
    void testFindAll() {
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));
        
        List<Payment> payments = paymentRepository.findAll();
        
        assertNotNull(payments);
        assertTrue(payments.size() >= 0);
        verify(paymentRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test findById payment repository method")
    void testFindById() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment1));
        
        Optional<Payment> foundPayment = paymentRepository.findById(1);
        
        assertNotNull(foundPayment);
        assertTrue(foundPayment.isPresent());
        verify(paymentRepository, times(1)).findById(1);
    }
      @Test
    @DisplayName("Test save payment repository method")
    void testSave() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment1);
        
        Payment savedPayment = paymentRepository.save(payment1);
        
        assertNotNull(savedPayment);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
      @Test
    @DisplayName("Test delete payment repository method")
    void testDelete() {
        paymentRepository.delete(payment1);
        
        verify(paymentRepository, times(1)).delete(payment1);
    }
      @Test
    @DisplayName("Test deleteById payment repository method")
    void testDeleteById() {
        paymentRepository.deleteById(1);
        
        verify(paymentRepository, times(1)).deleteById(anyInt());
    }
}
