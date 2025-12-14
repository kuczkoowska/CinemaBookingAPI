package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.screening.CreateScreeningDto;
import com.projekt.cinemabooking.dto.screening.ScreeningDto;
import com.projekt.cinemabooking.service.ScreeningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
