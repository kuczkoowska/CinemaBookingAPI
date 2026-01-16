package com.projekt.cinemabooking.controller.api;


import com.projekt.cinemabooking.dto.input.UpdateTicketPriceDto;
import com.projekt.cinemabooking.entity.TicketPrice;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.service.TicketPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-prices")
@RequiredArgsConstructor
@Tag(name = "Cennik", description = "Zarządzanie cenami biletów")
public class TicketPriceController {

    private final TicketPriceService ticketPriceService;

    @Operation(summary = "Pobierz cennik", description = "Zwraca listę wszystkich typów biletów i ich ceny.")
    @GetMapping
    public ResponseEntity<List<TicketPrice>> getAllPrices() {
        return ResponseEntity.ok(ticketPriceService.getAllPrices());
    }

    @Operation(summary = "Zaktualizuj cenę", description = "Zmienia cenę dla konkretnego typu biletu (np. NORMALNY, ULGOWY).")
    @PutMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketPrice> updatePrice(
            @Parameter(description = "Typ biletu (np. NORMALNY)") @PathVariable TicketType type,
            @Valid @RequestBody UpdateTicketPriceDto dto
    ) {
        return ResponseEntity.ok(ticketPriceService.updatePrice(type, dto.getPrice()));
    }
}