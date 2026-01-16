package com.projekt.cinemabooking.controller.api;

import com.projekt.cinemabooking.dto.input.CreateMovieDto;
import com.projekt.cinemabooking.dto.output.MovieDto;
import com.projekt.cinemabooking.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Filmy", description = "Zarządzanie bazą filmów")
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Pobierz listę filmów (z paginacją)")
    @GetMapping
    public ResponseEntity<Page<MovieDto>> getAllMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String genre,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(movieService.getMovies(title, genre, pageable));
    }

    @Operation(summary = "Pobierz listę filmów", description = "Zwraca listę wszystkich dostępnych filmów. Frontend")
    @GetMapping("/front")
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        List<MovieDto> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @Operation(summary = "Pobierz szczegóły filmu", description = "Zwraca pełne dane filmu na podstawie jego ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono film"),
            @ApiResponse(responseCode = "404", description = "Film o podanym ID nie istnieje")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Operation(summary = "Dodaj nowy film", description = "Dodaje nowy film.")
    @PostMapping//admin
    public ResponseEntity<Long> createMovie(@Valid @RequestBody CreateMovieDto createDto) {
        Long id = movieService.createMovie(createDto);
        return ResponseEntity.status(201).body(id);
    }

    @Operation(summary = "Edytuj dane filmu", description = "Edytuje dane filmu na podstawie jego ID.")
    @PutMapping("/{id}")
    public ResponseEntity<MovieDto> updateMovie(@Valid @RequestBody CreateMovieDto movieDto, @PathVariable Long id) {
        MovieDto movie = movieService.updateMovie(id, movieDto);
        return ResponseEntity.ok(movie);
    }

    @Operation(summary = "Usuń film", description = "Usuwa dane filmu na podstawie jego ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usunięto film"),
            @ApiResponse(responseCode = "404", description = "Film o podanym ID nie istnieje")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
