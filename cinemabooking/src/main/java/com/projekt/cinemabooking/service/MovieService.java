package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.movie.CreateMovieDto;
import com.projekt.cinemabooking.dto.movie.MovieDto;
import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.MovieMapper;
import com.projekt.cinemabooking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(movieMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MovieDto> getMoviesPage(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(movieMapper::mapToDto);
    }

    @Transactional
    public Long createMovie(CreateMovieDto createMovieDto) {
        Movie movie = movieMapper.mapToEntity(createMovieDto);

        Movie savedMovie = movieRepository.save(movie);
        return savedMovie.getId();
    }

    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return movieMapper.mapToDto(movie);
    }

    @Transactional
    public MovieDto updateMovie(Long id, CreateMovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        movieMapper.updateMovieFromDto(movieDto, movie);

        Movie savedMovie = movieRepository.save(movie);

        return movieMapper.mapToDto(savedMovie);
    }

    @Transactional
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        movieRepository.deleteById(id);
    }
}