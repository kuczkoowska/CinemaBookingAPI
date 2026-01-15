package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.screening.CreateScreeningDto;
import com.projekt.cinemabooking.dto.screening.ScreeningDto;
import com.projekt.cinemabooking.dto.seat.SeatDto;
import com.projekt.cinemabooking.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
@Tag(name = "Seanse", description = "Zarządzanie repertuarem")
public class ScreeningController {

    private final ScreeningService screeningService;

    @Operation(summary = "Dodaj nowy seans", description = "Wymaga ID filmu, ID sali i daty")
    @PostMapping
    public ResponseEntity<Long> createScreening(@Valid @RequestBody CreateScreeningDto screeningDto) {
        Long id = screeningService.createScreening(screeningDto);
        return ResponseEntity.status(201).body(id);
    }

    @Operation(summary = "Pobierz repertuar", description = "Zwraca wszystkie zaplanowane seanse.")
    @GetMapping
    public ResponseEntity<List<ScreeningDto>> getAllScreenings() {
        List<ScreeningDto> screenings = screeningService.getAllScreenings();
        return ResponseEntity.ok(screenings);
    }

    @Operation(summary = "Pobierz seanse dla filmu", description = "Zwraca listę godzin seansów dla konkretnego filmu")
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ScreeningDto>> getScreeningByMovieId(@PathVariable Long movieId, @RequestParam(required = false) LocalDate date) {
        List<ScreeningDto> screenings = screeningService.findByMovieId(movieId, date);
        return ResponseEntity.ok(screenings);
    }

    //zalogowany
    @Operation(summary = "Pobierz szczegóły seansu", description = "Zwraca szczegóły pojedynczego seansu (sala, film, godzina) na podstawie ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono seans"),
            @ApiResponse(responseCode = "404", description = "Seans nie istnieje")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ScreeningDto> getScreeningById(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getScreeningById(id));
    }

    //zalogowany
    @Operation(summary = "Pobierz dostępność miejsc", description = "Zwraca mapę miejsc dla seansu z informacją, które są wolne.")
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDto>> getSeatsForScreening(@PathVariable Long id) {
        return ResponseEntity.ok(screeningService.getSeatsForScreening(id));
    }

    // usun ??

}
