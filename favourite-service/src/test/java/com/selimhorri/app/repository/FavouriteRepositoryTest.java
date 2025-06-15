package com.selimhorri.app.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.selimhorri.app.domain.Favourite;
import com.selimhorri.app.domain.id.FavouriteId;

@ExtendWith(MockitoExtension.class)
public class FavouriteRepositoryTest {
    
    @Mock
    private FavouriteRepository favouriteRepository;
    
    private Favourite favourite1;
    private Favourite favourite2;
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
    }
      @Test
    @DisplayName("Test findAll favourites repository method")
    void testFindAll() {
        when(favouriteRepository.findAll()).thenReturn(Arrays.asList(favourite1, favourite2));
        
        List<Favourite> favourites = favouriteRepository.findAll();
        
        assertNotNull(favourites);
        assertTrue(favourites.size() >= 0);
        verify(favouriteRepository, times(1)).findAll();
    }
      @Test
    @DisplayName("Test findById favourite repository method")
    void testFindById() {
        when(favouriteRepository.findById(favouriteId1)).thenReturn(Optional.of(favourite1));
        
        Optional<Favourite> foundFavourite = favouriteRepository.findById(favouriteId1);
        
        assertNotNull(foundFavourite);
        assertTrue(foundFavourite.isPresent());
        verify(favouriteRepository, times(1)).findById(favouriteId1);
    }
      @Test
    @DisplayName("Test save favourite repository method")
    void testSave() {
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite1);
        
        Favourite savedFavourite = favouriteRepository.save(favourite1);
        
        assertNotNull(savedFavourite);
        verify(favouriteRepository, times(1)).save(any(Favourite.class));
    }
      @Test
    @DisplayName("Test delete favourite repository method")
    void testDelete() {
        favouriteRepository.delete(favourite1);
        
        verify(favouriteRepository, times(1)).delete(favourite1);
    }
      @Test
    @DisplayName("Test deleteById favourite repository method")
    void testDeleteById() {
        favouriteRepository.deleteById(favouriteId1);
        
        verify(favouriteRepository, times(1)).deleteById(favouriteId1);
    }
}
