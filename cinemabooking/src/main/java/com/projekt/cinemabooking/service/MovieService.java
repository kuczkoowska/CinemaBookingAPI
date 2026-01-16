package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.CreateMovieDto;
import com.projekt.cinemabooking.dto.output.MovieDto;
import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.Screening;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.MovieMapper;
import com.projekt.cinemabooking.repository.MovieRepository;
import com.projekt.cinemabooking.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final ScreeningRepository screeningRepository;

    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovieDto> getMovies(String title, String genreName, Pageable pageable) {
        MovieGenre genre = (genreName != null) ? MovieGenre.valueOf(genreName) : null;
        return movieRepository.searchMovies(title, genre, pageable).map(movieMapper::mapToDto);
    }

    @Transactional
    public Long createMovie(CreateMovieDto createMovieDto) {
        Movie movie = movieMapper.mapToEntity(createMovieDto);
        return movieRepository.save(movie).getId();
    }

    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film", id));
        return movieMapper.mapToDto(movie);
    }

    @Transactional
    public MovieDto updateMovie(Long id, CreateMovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Film", id));
        movieMapper.updateMovieFromDto(movieDto, movie);
        return movieMapper.mapToDto(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Film", id);
        }

        List<Screening> screenings = screeningRepository.findByMovieIdAndStartTimeAfter(id, LocalDateTime.MIN);
        if (!screenings.isEmpty()) {
            throw new IllegalStateException("Nie można usunąć filmu, który ma przypisane seanse (również archiwalne). Ukryj go zamiast usuwać.");
        }

        movieRepository.deleteById(id);
    }
}