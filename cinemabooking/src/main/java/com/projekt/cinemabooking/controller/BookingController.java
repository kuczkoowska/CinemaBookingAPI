package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.booking.BookingDto;
import com.projekt.cinemabooking.dto.seat.LockSeatsDto;
import com.projekt.cinemabooking.dto.ticket.UpdateTicketTypeDto;
import com.projekt.cinemabooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Rezerwacje", description = "Proces zakupu biletów")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Zablokuj miejsca", description = "Tworzy tymczasową rezerwację na 15 minut.")
    @PostMapping("/lock")
    public ResponseEntity<Long> lockSeats(@Valid @RequestBody LockSeatsDto lockSeatsDto) {
        Long bookingId = bookingService.lockSeats(lockSeatsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingId);
    }

    @Operation(summary = "Wybierz rodzaje biletów", description = "Aktualizuje bilety (np. z Normalny na Ulgowy) i przelicza cenę.")
    @PutMapping("/{id}/tickets")
    public ResponseEntity<BookingDto> updateTicketTypes(@PathVariable Long id, @Valid @RequestBody List<UpdateTicketTypeDto> tickets) {
        BookingDto booking = bookingService.updateTicketTypes(id, tickets);
        return ResponseEntity.ok(booking);
    }

    @Operation(summary = "Potwierdź rezerwację", description = "Zatwierdza rezerwację i zmienia status na OPLACONA.")
    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Anuluj rezerwacje", description = "Pozwala anulować rezerwację")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id, Authentication authentication) {
        bookingService.cancelBooking(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Zobacz szczegóły rezerwacji")
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Long id) {
        BookingDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
}
