package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.LockSeatsDto;
import com.projekt.cinemabooking.dto.input.UpdateTicketTypeDto;
import com.projekt.cinemabooking.dto.output.BookingDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.entity.enums.LogType;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.exception.SeatAlreadyTakenException;
import com.projekt.cinemabooking.mapper.BookingMapper;
import com.projekt.cinemabooking.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private ScreeningRepository screeningRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LogRepository logRepository;
    @Mock
    private TicketPriceRepository ticketPriceRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;


    @Test
    @DisplayName("Powinien poprawnie zablokować miejsca (Happy Path)")
    void shouldLockSeatsSuccessfully() {
        Long userId = 10L;
        LockSeatsDto dto = new LockSeatsDto();
        dto.setScreeningId(1L);
        dto.setSeatIds(List.of(5L));

        Screening screening = new Screening();
        screening.setId(1L);

        User user = new User();
        user.setId(userId);

        Seat seat = new Seat();
        seat.setId(5L);
        seat.setSeatNumber(5);

        TicketPrice price = new TicketPrice(1L, TicketType.NORMALNY, BigDecimal.valueOf(25.00));

        Booking savedBooking = new Booking();
        savedBooking.setId(123L);

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seatRepository.findById(5L)).thenReturn(Optional.of(seat));
        when(ticketRepository.existsByScreeningIdAndSeatId(eq(1L), eq(5L), anyList())).thenReturn(false);
        when(ticketPriceRepository.findByTicketType(TicketType.NORMALNY)).thenReturn(Optional.of(price));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingMapper.mapToDto(savedBooking)).thenReturn(new BookingDto());

        BookingDto result = bookingService.lockSeats(dto, userId);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy miejsce jest już zajęte")
    void shouldThrowExceptionWhenSeatIsTaken() {
        Long userId = 10L;
        LockSeatsDto dto = new LockSeatsDto();
        dto.setScreeningId(1L);
        dto.setSeatIds(List.of(5L));

        Screening screening = new Screening();
        screening.setId(1L);

        User user = new User();
        user.setId(userId);

        Seat seat = new Seat();
        seat.setId(5L);

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seatRepository.findById(5L)).thenReturn(Optional.of(seat));
        when(ticketPriceRepository.findByTicketType(TicketType.NORMALNY)).thenReturn(Optional.of(new TicketPrice(1L, TicketType.NORMALNY, BigDecimal.TEN)));

        when(ticketRepository.existsByScreeningIdAndSeatId(eq(1L), eq(5L), anyList())).thenReturn(true);

        assertThrows(SeatAlreadyTakenException.class, () -> bookingService.lockSeats(dto, userId));
        verify(bookingRepository, never()).save(any());
    }


    @Test
    @DisplayName("Powinien zaktualizować typy biletów i przeliczyć cenę")
    void shouldUpdateTicketTypes() {
        Long userId = 10L;
        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setTotalAmount(BigDecimal.valueOf(25.0));

        Ticket ticket = new Ticket();
        ticket.setId(100L);
        ticket.setTicketType(TicketType.NORMALNY);
        ticket.setPrice(BigDecimal.valueOf(25.0));

        booking.setTickets(new ArrayList<>(List.of(ticket)));

        UpdateTicketTypeDto updateDto = new UpdateTicketTypeDto(100L, TicketType.ULGOWY);
        List<UpdateTicketTypeDto> updates = List.of(updateDto);

        TicketPrice ulgowyPrice = new TicketPrice(2L, TicketType.ULGOWY, BigDecimal.valueOf(15.0));
        when(ticketPriceRepository.findAll()).thenReturn(List.of(ulgowyPrice));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.mapToDto(any())).thenReturn(new BookingDto());

        bookingService.updateTicketTypes(1L, updates, userId);

        assertThat(ticket.getTicketType()).isEqualTo(TicketType.ULGOWY);
        assertThat(ticket.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
        assertThat(booking.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(15.0));
        verify(bookingRepository).save(booking);
    }


    @Test
    @DisplayName("Powinien potwierdzić rezerwację")
    void shouldConfirmBooking() {
        Long userId = 10L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@test.pl");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setExpirationTime(LocalDateTime.now().plusMinutes(10));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.confirmBooking(1L, userId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.OPLACONA);
        assertThat(booking.getExpirationTime()).isNull();
        verify(bookingRepository).save(booking);
        verify(logRepository).saveLog(eq(LogType.INFO), anyString(), eq("test@test.pl"));
    }


    @Test
    @DisplayName("Powinien pozwolić użytkownikowi anulować własną rezerwację")
    void shouldCancelBookingByUser() {
        Long userId = 10L;
        User user = new User();
        user.setId(userId);
        user.setEmail("jan@test.pl");

        Screening screening = new Screening();
        screening.setStartTime(LocalDateTime.now().plusDays(1));

        Ticket ticket = new Ticket();
        ticket.setScreening(screening);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setStatus(BookingStatus.OPLACONA);
        booking.setTickets(List.of(ticket));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L, userId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ANULOWANA);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Nie powinien pozwolić anulować cudzej rezerwacji")
    void shouldThrowExceptionWhenCancelingSomeoneElseBooking() {
        Long ownerId = 10L;
        Long thiefId = 99L;

        User owner = new User();
        owner.setId(ownerId);

        Booking booking = new Booking();
        booking.setUser(owner);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class,
                () -> bookingService.cancelBooking(1L, thiefId));
    }
}