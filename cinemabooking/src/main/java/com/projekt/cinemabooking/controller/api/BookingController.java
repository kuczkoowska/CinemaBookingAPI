package com.projekt.cinemabooking.controller.api;

import com.projekt.cinemabooking.dto.input.ConfirmBookingDto;
import com.projekt.cinemabooking.dto.input.LockSeatsDto;
import com.projekt.cinemabooking.dto.input.UpdateTicketTypeDto;
import com.projekt.cinemabooking.dto.output.BookingDto;
import com.projekt.cinemabooking.security.CustomUserDetails;
import com.projekt.cinemabooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Rezerwacje", description = "Proces zakupu biletów")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Pobierz moje rezerwacje (Paginacja)")
    @GetMapping("/my")
    public ResponseEntity<Page<BookingDto>> getMyBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(bookingService.getMyBookings(userDetails.getUser().getId(), pageable));
    }

    @PostMapping("/lock")
    public ResponseEntity<BookingDto> lockSeats(
            @Valid @RequestBody LockSeatsDto lockSeatsDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long currentUserId = userDetails.getUser().getId();
        BookingDto booking = bookingService.lockSeats(lockSeatsDto, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @Operation(summary = "Wybierz rodzaje biletów")
    @PutMapping("/{id}/tickets")
    public ResponseEntity<BookingDto> updateTicketTypes(
            @PathVariable Long id,
            @Valid @RequestBody List<UpdateTicketTypeDto> tickets,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BookingDto booking = bookingService.updateTicketTypes(id, tickets, userDetails.getUser().getId());
        return ResponseEntity.ok(booking);
    }

    @Operation(summary = "Potwierdź rezerwację")
    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> confirmBooking(
            @PathVariable Long id,
            @RequestBody(required = false) ConfirmBookingDto contactDetails, // Opcjonalne dane z formularza
            @AuthenticationPrincipal CustomUserDetails userDetails // <-- MUSI BYĆ (Spring Security to wymusi)
    ) {
        // Przekazujemy ID usera na sztywno
        bookingService.confirmBooking(id, userDetails.getUser().getId(), contactDetails);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Anuluj rezerwacje", description = "Pozwala anulować rezerwację")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookingService.cancelBooking(id, userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Zobacz szczegóły rezerwacji")
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        BookingDto booking = bookingService.getBookingById(id, userDetails.getUser().getId());
        return ResponseEntity.ok(booking);
    }
}
