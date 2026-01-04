package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.screening.CreateScreeningDto;
import com.projekt.cinemabooking.dto.screening.ScreeningDto;
import com.projekt.cinemabooking.dto.seat.SeatDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.ScreeningMapper;
import com.projekt.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final TheaterRoomRepository theaterRoomRepository;
    private final ScreeningMapper screeningMapper;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public Long createScreening(CreateScreeningDto createScreeningDto) {
        Movie movie = movieRepository.findById(createScreeningDto.getMovieId()).orElseThrow(() -> new ResourceNotFoundException("Film", createScreeningDto.getMovieId()));

        TheaterRoom room = theaterRoomRepository.findById(createScreeningDto.getTheaterRoomId()).orElseThrow(() -> new ResourceNotFoundException("Sala", createScreeningDto.getTheaterRoomId()));

        LocalDateTime startTime = createScreeningDto.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(movie.getDurationMinutes() + 20);

        List<Screening> conflicts = screeningRepository.findOverlappingScreenings(room.getId(), startTime, endTime);

        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException("Sala jest zajęta w tym terminie! Kolizja z seansem: " + conflicts.getFirst().getMovie().getTitle() + " - " + conflicts.getFirst().getId());
        }

        Screening screening = new Screening();
        screeningMapper.updateScreeningFromDto(createScreeningDto, screening);
        screening.setMovie(movie);
        screening.setTheaterRoom(room);
        screening.setEndTime(endTime);

        return screeningRepository.save(screening).getId();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> getAllScreenings() {
        return screeningRepository.findAll().stream()
                .map(screeningMapper::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> findByMovieId(Long id, LocalDate date) {
        List<Screening> screenings;

        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            screenings = screeningRepository.findByMovieIdAndStartTimeBetween(id, startOfDay, endOfDay);
        } else {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endOfDay = now.toLocalDate().atTime(LocalTime.MAX);

            screenings = screeningRepository.findByMovieIdAndStartTimeBetween(id, now, endOfDay);
        }

        return screenings.stream()
                .map(screeningMapper::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScreeningDto getScreeningById(Long id) {
        Screening screening = screeningRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seans", id));
        return screeningMapper.mapToDto(screening);
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsForScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId).orElseThrow(() -> new ResourceNotFoundException("Seans", screeningId));

        List<Seat> allSeats = seatRepository.findByTheaterRoomId(screening.getTheaterRoom().getId());

        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningId);

        Set<Long> takenSeats = tickets.stream()
                .filter(ticket -> ticket.getBooking().getStatus() != BookingStatus.ANULOWANA)
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());

        return allSeats.stream()
                .map(seat -> SeatDto.builder()
                        .id(seat.getId())
                        .rowNumber(seat.getRowNumber())
                        .seatNumber(seat.getSeatNumber())
                        .available(!takenSeats.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}
