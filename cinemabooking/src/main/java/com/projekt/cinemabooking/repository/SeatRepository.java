package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Seat;
import com.projekt.cinemabooking.entity.TheaterRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheaterRoomId(Long theaterRoomId);

    void deleteAllByTheaterRoom(TheaterRoom theaterRoom);
}
