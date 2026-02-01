package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Booking;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByUserIdOrderByBookingTimeDesc(Long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.expirationTime IS NOT NULL AND b.expirationTime < :now")
    List<Booking> findExpiredBookings(@Param("status") BookingStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT DISTINCT b FROM Booking b " +
           "LEFT JOIN FETCH b.tickets t " +
           "LEFT JOIN FETCH t.screening s " +
           "LEFT JOIN FETCH s.movie " +
           "LEFT JOIN FETCH s.theaterRoom " +
           "LEFT JOIN FETCH t.seat " +
           "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithDetails(@Param("bookingId") Long bookingId);
}
