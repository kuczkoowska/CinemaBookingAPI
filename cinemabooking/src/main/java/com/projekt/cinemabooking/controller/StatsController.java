package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.output.SalesStatsDto;
import com.projekt.cinemabooking.repository.SalesStatisticsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "Admin - statystyki", description = "Statystyki sprzedaży")
public class StatsController {

    private final SalesStatisticsRepository statsRepository;

    @Operation(summary = "Raport dzienny", description = "Zwraca przychód i liczbę biletów.")
    @GetMapping
    public ResponseEntity<List<SalesStatsDto>> getDailySales(
            @Parameter(description = "Po czym sortować? Opcje: data(date), przychód(revenue), bilety(tickets)")
            @RequestParam(defaultValue = "date") String sortBy,

            @Parameter(description = "Kierunek? ASC (rosnąco) lub DESC (malejąco)")
            @RequestParam(defaultValue = "DESC") String dir
    ) {
        return ResponseEntity.ok(statsRepository.getSalesByDate(sortBy, dir));
    }
}
