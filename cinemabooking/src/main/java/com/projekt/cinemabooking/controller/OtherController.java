package com.projekt.cinemabooking.controller;


import com.projekt.cinemabooking.entity.TicketPrice;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.TicketPriceRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/other")
@RequiredArgsConstructor
@Tag(name = "Cennik", description = "Zarządzanie cenami biletów (Tylko Admin)")
public class OtherController {

    private final TicketPriceRepository ticketPriceRepository;

    @GetMapping
    public ResponseEntity<List<TicketPrice>> getAllPrices() {
        return ResponseEntity.ok(ticketPriceRepository.findAll());
    }

    @PutMapping("/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketPrice> updatePrice(@PathVariable TicketType type, @RequestBody Double newPrice) {
        TicketPrice ticketPrice = ticketPriceRepository.findByTicketType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Cennik"));

        ticketPrice.setPrice(newPrice);
        return ResponseEntity.ok(ticketPriceRepository.save(ticketPrice));
    }
}
