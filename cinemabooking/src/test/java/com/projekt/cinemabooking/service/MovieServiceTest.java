package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.CreateMovieDto;
import com.projekt.cinemabooking.dto.output.MovieDto;
import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.Screening;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.MovieMapper;
import com.projekt.cinemabooking.repository.MovieRepository;
import com.projekt.cinemabooking.repository.ScreeningRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieMapper movieMapper;
    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private MovieService movieService;

    @Test
    @DisplayName("Powinien utworzyć nowy film")
    void shouldCreateMovie() {
        CreateMovieDto dto = new CreateMovieDto();
        dto.setTitle("Matrix");

        Movie movie = new Movie();
        movie.setTitle("Matrix");

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("Matrix");

        when(movieMapper.mapToEntity(dto)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(savedMovie);

        Long id = movieService.createMovie(dto);

        assertThat(id).isEqualTo(1L);
        verify(movieRepository).save(movie);
    }

    @Test
    @DisplayName("Powinien zwrócić film po ID")
    void shouldGetMovieById() {
        Movie movie = new Movie();
        movie.setId(1L);
        MovieDto dto = new MovieDto();
        dto.setId(1L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieMapper.mapToDto(movie)).thenReturn(dto);

        MovieDto result = movieService.getMovieById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Powinien zaktualizować film")
    void shouldUpdateMovie() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Old Title");

        CreateMovieDto updateDto = new CreateMovieDto();
        updateDto.setTitle("New Title");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);

        MovieDto resultDto = new MovieDto();
        resultDto.setTitle("New Title");
        when(movieMapper.mapToDto(movie)).thenReturn(resultDto);

        MovieDto result = movieService.updateMovie(1L, updateDto);

        assertThat(result.getTitle()).isEqualTo("New Title");
        verify(movieMapper).updateMovieFromDto(updateDto, movie);
    }

    @Test
    @DisplayName("Powinien usunąć film jeśli istnieje i nie ma seansów")
    void shouldDeleteMovie() {
        Long movieId = 1L;
        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(screeningRepository.findByMovieIdAndStartTimeAfter(eq(movieId), any()))
                .thenReturn(List.of());

        movieService.deleteMovie(movieId);

        verify(movieRepository).deleteById(movieId);
    }

    @Test
    @DisplayName("Powinien ZABRONIĆ usunięcia filmu, który ma zaplanowane seanse")
    void shouldThrowExceptionWhenDeletingMovieWithScreenings() {
        Long movieId = 1L;
        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(screeningRepository.findByMovieIdAndStartTimeAfter(eq(movieId), any()))
                .thenReturn(List.of(new Screening()));

        assertThrows(IllegalStateException.class, () -> movieService.deleteMovie(movieId));

        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek przy próbie usunięcia nieistniejącego filmu")
    void shouldThrowExceptionWhenDeletingNonExistentMovie() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(1L));
        verify(movieRepository, never()).deleteById(any());
    }
}

