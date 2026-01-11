package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
