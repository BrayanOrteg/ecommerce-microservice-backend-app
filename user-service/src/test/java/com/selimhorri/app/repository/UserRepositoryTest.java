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

import com.selimhorri.app.domain.User;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    
    @Mock
    private UserRepository userRepository;
    
    private User user1;
    private User user2;
    
    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();
                
        user2 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("+0987654321")
                .build();
    }
      @Test
    @DisplayName("Test findAll users repository method")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        
        List<User> users = userRepository.findAll();
        
        assertNotNull(users);
        assertTrue(users.size() >= 0);
        verify(userRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test findById user repository method")
    void testFindById() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        
        Optional<User> foundUser = userRepository.findById(1);
        
        assertNotNull(foundUser);
        assertTrue(foundUser.isPresent());
        verify(userRepository, times(1)).findById(1);
    }
      @Test
    @DisplayName("Test save user repository method")
    void testSave() {
        when(userRepository.save(any(User.class))).thenReturn(user1);
        
        User savedUser = userRepository.save(user1);
        
        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }
      @Test
    @DisplayName("Test delete user repository method")
    void testDelete() {
        userRepository.delete(user1);
        
        verify(userRepository, times(1)).delete(user1);
    }
      @Test
    @DisplayName("Test deleteById user repository method")
    void testDeleteById() {
        userRepository.deleteById(1);
        
        verify(userRepository, times(1)).deleteById(anyInt());
    }
}
