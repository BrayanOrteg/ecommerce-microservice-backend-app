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

import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.helper.UserMappingHelper;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    
    private MockedStatic<UserMappingHelper> mappingHelperMock;
      @BeforeEach
    void setUp() {
        user1 = User.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .imageUrl("http://example.com/john.jpg")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();
                
        user2 = User.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .imageUrl("http://example.com/jane.jpg")
                .email("jane.smith@example.com")
                .phone("+0987654321")
                .build();
                
        userDto1 = UserDto.builder()
                .userId(1)
                .firstName("John")
                .lastName("Doe")
                .imageUrl("http://example.com/john.jpg")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();
                
        userDto2 = UserDto.builder()
                .userId(2)
                .firstName("Jane")
                .lastName("Smith")
                .imageUrl("http://example.com/jane.jpg")
                .email("jane.smith@example.com")
                .phone("+0987654321")
                .build();
        
        mappingHelperMock = mockStatic(UserMappingHelper.class);
    }
    
    @AfterEach
    void tearDown() {
        mappingHelperMock.close();
    }
      @Test
    @DisplayName("Test findAll users")
    void testFindAll() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        mappingHelperMock.when(() -> UserMappingHelper.map(user1)).thenReturn(userDto1);
        mappingHelperMock.when(() -> UserMappingHelper.map(user2)).thenReturn(userDto2);
        
        List<UserDto> users = userService.findAll();
        
        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test find user by existing ID")
    void testFindByIdWithExistingUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        mappingHelperMock.when(() -> UserMappingHelper.map(user1)).thenReturn(userDto1);
        
        UserDto foundUser = userService.findById(1);
        
        assertNotNull(foundUser);
        assertEquals(1, foundUser.getUserId());
        assertEquals("John", foundUser.getFirstName());
        verify(userRepository, times(1)).findById(1);
    }    @Test
    @DisplayName("Test save user")
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(user1);
        mappingHelperMock.when(() -> UserMappingHelper.map(userDto1)).thenReturn(user1);
        mappingHelperMock.when(() -> UserMappingHelper.map(user1)).thenReturn(userDto1);
        
        UserDto savedUser = userService.save(userDto1);
        
        assertNotNull(savedUser);
        verify(userRepository, times(1)).save(any(User.class));
    }
      @Test
    @DisplayName("Test delete user by ID")
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(anyInt());
        
        userService.deleteById(1);
        
        verify(userRepository, times(1)).deleteById(1);
    }
}
