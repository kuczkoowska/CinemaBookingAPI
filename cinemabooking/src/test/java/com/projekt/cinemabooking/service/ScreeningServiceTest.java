package com.projekt.cinemabooking.service;


import com.projekt.cinemabooking.dto.screening.CreateScreeningDto;
import com.projekt.cinemabooking.dto.seat.SeatDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.mapper.ScreeningMapper;
import com.projekt.cinemabooking.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {

    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private TheaterRoomRepository theaterRoomRepository;
    @Mock
    private ScreeningMapper screeningMapper;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private ScreeningService screeningService;

    @Test
    @DisplayName("Powinien utworzyć seans, gdy nie ma kolizji")
    void shouldCreateScreeningWhenNoConflict() {
        CreateScreeningDto dto = new CreateScreeningDto();
        dto.setMovieId(1L);
        dto.setTheaterRoomId(1L);
        dto.setStartTime(LocalDateTime.now().plusHours(2));

        Movie movie = new Movie();
        movie.setId(1L);
        movie.setDurationMinutes(120);

        TheaterRoom room = new TheaterRoom();
        room.setId(1L);

        Screening savedScreening = new Screening();
        savedScreening.setId(10L);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(theaterRoomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(screeningRepository.findOverlappingScreenings(eq(1L), any(), any())).thenReturn(Collections.emptyList());
        when(screeningRepository.save(any(Screening.class))).thenReturn(savedScreening);

        Long result = screeningService.createScreening(dto);

        assertThat(result).isEqualTo(10L);
        verify(screeningRepository).save(any(Screening.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy seans nakłada się na inny")
    void shouldThrowExceptionWhenScreeningOverlaps() {
        CreateScreeningDto dto = new CreateScreeningDto();
        dto.setMovieId(1L);
        dto.setTheaterRoomId(1L);
        dto.setStartTime(LocalDateTime.now());

        Movie movie = new Movie();
        movie.setDurationMinutes(120);
        movie.setTitle("Movie 1");

        TheaterRoom room = new TheaterRoom();
        room.setId(1L);

        Screening conflict = new Screening();
        conflict.setId(5L);
        conflict.setMovie(movie);

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(theaterRoomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(screeningRepository.findOverlappingScreenings(eq(1L), any(), any())).thenReturn(List.of(conflict));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> screeningService.createScreening(dto));
        assertThat(ex.getMessage()).contains("Sala jest zajęta");

        verify(screeningRepository, never()).save(any());
    }

    @Test
    @DisplayName("Powinien poprawnie oznaczyć zajęte miejsca")
    void shouldReturnSeatsWithAvailability() {
        Long screeningId = 100L;
        TheaterRoom room = new TheaterRoom();
        room.setId(1L);

        Screening screening = new Screening();
        screening.setId(screeningId);
        screening.setTheaterRoom(room);

        Seat seat1 = new Seat();
        seat1.setId(1L);
        seat1.setSeatNumber(1);
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setSeatNumber(2);
        List<Seat> allSeats = List.of(seat1, seat2);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.OPLACONA);
        Ticket ticket = new Ticket();
        ticket.setSeat(seat1);
        ticket.setBooking(booking);

        when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        when(seatRepository.findByTheaterRoomId(1L)).thenReturn(allSeats);
        when(ticketRepository.findAllByScreeningId(screeningId)).thenReturn(List.of(ticket));

        List<SeatDto> result = screeningService.getSeatsForScreening(screeningId);

        assertThat(result).hasSize(2);

        SeatDto dto1 = result.stream().filter(s -> s.getId().equals(1L)).findFirst().get();
        assertThat(dto1.isAvailable()).isFalse();

        SeatDto dto2 = result.stream().filter(s -> s.getId().equals(2L)).findFirst().get();
        assertThat(dto2.isAvailable()).isTrue();
    }
}