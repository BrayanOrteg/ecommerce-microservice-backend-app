package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;
import com.selimhorri.app.dto.FavouriteDto;
import com.selimhorri.app.repository.FavouriteRepository;
import com.selimhorri.app.service.impl.FavouriteServiceImpl;

@ExtendWith(MockitoExtension.class)
public class FavouriteServiceTest {
    
    @Mock
    private FavouriteRepository favouriteRepository;
    
    @Mock
    private RestTemplate restTemplate;
    
    @InjectMocks
    private FavouriteServiceImpl favouriteService;
      private Favourite favourite1;
    private Favourite favourite2;
    private FavouriteDto favouriteDto1;
    private FavouriteId favouriteId1;
      @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        favouriteId1 = new FavouriteId(1, 101, now);
        
        favourite1 = Favourite.builder()
                .userId(1)
                .productId(101)
                .likeDate(now)
                .build();
                
        favourite2 = Favourite.builder()
                .userId(2)
                .productId(102)
                .likeDate(now.plusDays(1))
                .build();
                
        favouriteDto1 = FavouriteDto.builder()
                .userId(1)
                .productId(101)
                .likeDate(now)
                .build();
    }
      @Test
    @DisplayName("Test repository findAll interaction")
    void testFindAll() {
        when(favouriteRepository.findAll()).thenReturn(Arrays.asList(favourite1, favourite2));
        
        List<FavouriteDto> favourites = favouriteService.findAll();
        
        assertNotNull(favourites);
        verify(favouriteRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test repository save interaction")
    void testSaveFavourite() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite1);
        
        FavouriteDto savedFavourite = favouriteService.save(favouriteDto1);
        
        assertNotNull(savedFavourite);
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }
      @Test
    @DisplayName("Test repository deleteById interaction")
    void testDeleteById() {
        doNothing().when(favouriteRepository).deleteById(any(FavouriteId.class));
        
        favouriteService.deleteById(favouriteId1);
        
        verify(favouriteRepository, times(1)).deleteById(favouriteId1);
    }
}
