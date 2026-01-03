package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.admin.SalesStatsDto;
import com.projekt.cinemabooking.repository.SalesStatisticsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "Admin - Statystyki", description = "Raporty sprzedażowe (JdbcTemplate)")
public class StatsController {

    private final SalesStatisticsRepository statsRepository;

    @Operation(summary = "Raport dzienny", description = "Zwraca przychód i liczbę biletów na każdy dzień.")
    @GetMapping
    public ResponseEntity<List<SalesStatsDto>> getDailySales() {
        return ResponseEntity.ok(statsRepository.getSalesByDate());
    }
}
