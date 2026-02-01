package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.entity.Booking;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupService {

    private final BookingRepository bookingRepository;

    /**
     * Sprawdza co minutę czy są wygasłe rezerwacje i anuluje je.
     * Rezerwacja jest wygasła jeśli:
     * - Ma status OCZEKUJE
     * - Czas expirationTime minął
     */
    @Scheduled(fixedRate = 60000) // co 60 sekund
    @Transactional
    public void cleanupExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(BookingStatus.OCZEKUJE, now);
        
        if (!expiredBookings.isEmpty()) {
            log.info("Znaleziono {} wygasłych rezerwacji do anulowania", expiredBookings.size());
            
            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.ANULOWANA);
                bookingRepository.save(booking);
                log.debug("Anulowano rezerwację ID: {}", booking.getId());
            }
        }
    }
}
