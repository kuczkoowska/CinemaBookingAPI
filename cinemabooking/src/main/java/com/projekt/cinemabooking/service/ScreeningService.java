package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.CreateScreeningDto;
import com.projekt.cinemabooking.dto.output.ScreeningDto;
import com.projekt.cinemabooking.dto.output.SeatDto;
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
    public Long createScreening(CreateScreeningDto dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Film", dto.getMovieId()));

        TheaterRoom room = theaterRoomRepository.findById(dto.getTheaterRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala", dto.getTheaterRoomId()));

        LocalDateTime endTime = dto.getStartTime().plusMinutes(movie.getDurationMinutes() + 20);

        List<Screening> conflicts = screeningRepository.findOverlappingScreenings(room.getId(), dto.getStartTime(), endTime);
        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Sala jest zajęta");
        }

        Screening screening = new Screening();
        screening.setStartTime(dto.getStartTime());
        screening.setEndTime(endTime);
        screening.setMovie(movie);
        screening.setTheaterRoom(room);

        return screeningRepository.save(screening).getId();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> getAllScreenings() {
        return screeningRepository.findAll().stream()
                .map(this::mapToDtoWithSeats)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> findByMovieId(Long id, LocalDate date) {
        List<Screening> screenings;
        if (date != null) {
            screenings = screeningRepository.findByMovieIdAndStartTimeBetween(id, date.atStartOfDay(), date.atTime(LocalTime.MAX));
        } else {
            // Pobierz wszystkie seanse od początku dzisiejszego dnia
            screenings = screeningRepository.findByMovieIdAndStartTimeAfter(id, LocalDate.now().atStartOfDay());
        }
        return screenings.stream().map(this::mapToDtoWithSeats).toList();
    }

    @Transactional(readOnly = true)
    public ScreeningDto getScreeningById(Long id) {
        Screening screening = screeningRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seans", id));
        return mapToDtoWithSeats(screening);
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsForScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new ResourceNotFoundException("Seans", screeningId));

        List<Seat> allSeats = seatRepository.findByTheaterRoomId(screening.getTheaterRoom().getId());

        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningId);

        Set<Long> takenSeatIds = tickets.stream()
                .filter(t -> t.getBooking().getStatus() != BookingStatus.ANULOWANA)
                .map(t -> t.getSeat().getId())
                .collect(Collectors.toSet());

        return allSeats.stream()
                .map(seat -> SeatDto.builder()
                        .id(seat.getId())
                        .rowNumber(seat.getRowNumber())
                        .seatNumber(seat.getSeatNumber())
                        .available(!takenSeatIds.contains(seat.getId()))
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> getScreeningsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return screeningRepository.findAllByStartTimeBetweenOrderByStartTimeAsc(startOfDay, endOfDay)
                .stream()
                .map(this::mapToDtoWithSeats)
                .toList();
    }

    private ScreeningDto mapToDtoWithSeats(Screening screening) {
        ScreeningDto dto = screeningMapper.mapToDto(screening);

        // Pobierz całkowitą liczbę miejsc w sali
        int totalSeats = seatRepository.countByTheaterRoomId(screening.getTheaterRoom().getId());

        // Pobierz liczbę zajętych miejsc (z aktywnych rezerwacji)
        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screening.getId());
        int takenSeats = (int) tickets.stream()
                .filter(t -> t.getBooking().getStatus() != BookingStatus.ANULOWANA)
                .count();

        dto.setTotalSeats(totalSeats);
        dto.setAvailableSeats(totalSeats - takenSeats);

        return dto;
    }
}
