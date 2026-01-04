package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.booking.CreateBookingDto;
import com.projekt.cinemabooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Rezerwacje", description = "Proces zakupu biletów")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Utwórz rezerwację", description = "Kupuje bilety. Blokuje miejsca. Zwraca ID zamówienia.")
    @PostMapping
    public ResponseEntity<Long> createBooking(@Valid @RequestBody CreateBookingDto createBookingDto) {
        Long bookingId = bookingService.createBooking(createBookingDto);
        return ResponseEntity.status(201).body(bookingId);
    }

    @Operation(summary = "Anuluj rezerwacje", description = "Pozwala anulować rezerwację")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id, Authentication authentication) {
        bookingService.cancelBooking(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
