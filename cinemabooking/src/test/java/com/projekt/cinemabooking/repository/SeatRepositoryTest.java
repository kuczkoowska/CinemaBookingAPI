package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Seat;
import com.projekt.cinemabooking.entity.TheaterRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TheaterRoomRepository theaterRoomRepository;

    private TheaterRoom room;

    @BeforeEach
    void setUp() {
        room = new TheaterRoom();
        room.setName("Sala A");
        theaterRoomRepository.save(room);
    }

    @Test
    void shouldFindSeatsByTheaterRoomId() {
        Seat s1 = new Seat();
        s1.setTheaterRoom(room);
        s1.setRowNumber(1);
        s1.setSeatNumber(1);
        seatRepository.save(s1);

        Seat s2 = new Seat();
        s2.setTheaterRoom(room);
        s2.setRowNumber(1);
        s2.setSeatNumber(2);
        seatRepository.save(s2);

        List<Seat> seats = seatRepository.findByTheaterRoomId(room.getId());

        assertThat(seats).hasSize(2);
    }

    @Test
    void shouldDeleteAllSeatsForTheaterRoom() {
        Seat s1 = new Seat();
        s1.setTheaterRoom(room);
        s1.setRowNumber(1);
        s1.setSeatNumber(1);
        seatRepository.save(s1);

        seatRepository.deleteAllByTheaterRoom(room);

        List<Seat> seats = seatRepository.findByTheaterRoomId(room.getId());
        assertThat(seats).isEmpty();
    }
}
