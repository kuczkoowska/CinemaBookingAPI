package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.seat.CreateSeatsDto;
import com.projekt.cinemabooking.dto.theater.TheaterRoomDto;
import com.projekt.cinemabooking.entity.Seat;
import com.projekt.cinemabooking.entity.TheaterRoom;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.ScreeningRepository;
import com.projekt.cinemabooking.repository.SeatRepository;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterRoomService {

    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private final ScreeningRepository screeningRepository;


    @Transactional
    public void generateSeatsForRoom(Long roomId, CreateSeatsDto dto) {
        TheaterRoom room = theaterRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Sala", roomId));

        List<Seat> newSeats = new ArrayList<>();

        for (int row = 1; row <= dto.getRows(); row++) {
            for (int num = 1; num <= dto.getSeatsPerRow(); num++) {
                Seat seat = new Seat();
                seat.setRowNumber(row);
                seat.setSeatNumber(num);
                seat.setTheaterRoom(room);
                newSeats.add(seat);
            }
        }

        seatRepository.saveAll(newSeats);
    }

    @Transactional
    public Long createRoom(TheaterRoomDto dto) {
        TheaterRoom room = new TheaterRoom();
        room.setName(dto.getName());
        return theaterRoomRepository.save(room).getId();
    }

    @Transactional
    public void editRoom(Long id, TheaterRoomDto dto) {
        TheaterRoom room = theaterRoomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Sala", id));

        room.setName(dto.getName());
        theaterRoomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        TheaterRoom room = theaterRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala", id));

        boolean hasScreenings = screeningRepository.existsByTheaterRoomId(id);

        if (hasScreenings) {
            throw new IllegalStateException("Nie można usunąć sali, w której zaplanowane są seanse! Najpierw usuń seanse.");
        }

        seatRepository.deleteAllByTheaterRoom(room);
        theaterRoomRepository.delete(room);
    }

    @Transactional
    public void updateSeatLayout(Long roomId, CreateSeatsDto dto) {
        TheaterRoom room = theaterRoomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Sala", roomId));

        boolean hasFutureScreenings = screeningRepository.existsByTheaterRoomIdAndStartTimeAfter(roomId, LocalDateTime.now());

        if (hasFutureScreenings) {
            throw new IllegalStateException(
                    "Nie można zmienić układu sali! W tej sali są zaplanowane przyszłe seanse."
            );
        }

        seatRepository.deleteAllByTheaterRoom(room);
        seatRepository.flush();

        generateSeatsForRoom(roomId, dto);
    }
}
