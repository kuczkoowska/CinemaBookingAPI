package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.CreateRoomDto;
import com.projekt.cinemabooking.entity.Seat;
import com.projekt.cinemabooking.entity.TheaterRoom;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.ScreeningRepository;
import com.projekt.cinemabooking.repository.SeatRepository;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TheaterRoomService {

    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private final ScreeningRepository screeningRepository;


    @Transactional
    public Long createRoom(CreateRoomDto dto) {
        TheaterRoom room = new TheaterRoom();
        room.setName(dto.getName());
        room.setTotalRows(dto.getRows());
        room.setSeatsPerRow(dto.getSeatsPerRow());

        room = theaterRoomRepository.save(room);

        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= dto.getRows(); row++) {
            for (int num = 1; num <= dto.getSeatsPerRow(); num++) {
                Seat seat = Seat.builder()
                        .rowNumber(row)
                        .seatNumber(num)
                        .theaterRoom(room)
                        .build();
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);

        return room.getId();
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

}
