package com.example.weather.service;

import com.example.weather.model.CreateFavouriteRequest;
import com.example.weather.model.Favourite;
import com.example.weather.repository.FavouriteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FavouriteServiceTest {

    private final FavouriteRepository repository = mock(FavouriteRepository.class);
    private final FavouriteService service = new FavouriteService(repository);

    @Test
    void shouldAddFavourite() {
        when(repository.save(any())).thenAnswer(invocation -> {
            Favourite favourite = invocation.getArgument(0);
            ReflectionTestUtils.setField(favourite, "id", 1L);
            return favourite;
        });

        var result = service.add(new CreateFavouriteRequest("London"));

        assertEquals(1L, result.id());
        assertEquals("London", result.location());
    }

    @Test
    void shouldListFavourites() {
        Favourite favourite = new Favourite("Manchester");
        ReflectionTestUtils.setField(favourite, "id", 2L);
        when(repository.findAll()).thenReturn(List.of(favourite));

        var result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(2L, result.getFirst().id());
        assertEquals("Manchester", result.getFirst().location());
    }

    @Test
    void shouldDeleteExistingFavourite() {
        when(repository.existsById(3L)).thenReturn(true);

        service.delete(3L);

        verify(repository).deleteById(3L);
    }

    @Test
    void shouldRejectMissingFavourite() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.delete(99L));
    }
}
