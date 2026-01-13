package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.booking.BookingDto;
import com.projekt.cinemabooking.dto.seat.LockSeatsDto;
import com.projekt.cinemabooking.dto.ticket.UpdateTicketTypeDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
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
        LockSeatsDto dto = new LockSeatsDto();
        dto.setScreeningId(1L);
        dto.setUserId(10L);
        dto.setSeatIds(List.of(5L));

        Screening screening = new Screening();
        screening.setId(1L);

        User user = new User();
        user.setId(10L);

        Seat seat = new Seat();
        seat.setId(5L);
        seat.setSeatNumber(5);

        TicketPrice price = new TicketPrice(1L, TicketType.NORMALNY, 25.00);
        Booking savedBooking = new Booking();
        savedBooking.setId(123L);

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(seatRepository.findById(5L)).thenReturn(Optional.of(seat));
        when(ticketRepository.existsByScreeningIdAndSeatId(1L, 5L)).thenReturn(false); // Miejsce wolne
        when(ticketPriceRepository.findByTicketType(TicketType.NORMALNY)).thenReturn(Optional.of(price));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Long resultId = bookingService.lockSeats(dto);

        assertThat(resultId).isEqualTo(123L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Powinien rzucić wyjątek, gdy miejsce jest już zajęte (Error Case)")
    void shouldThrowExceptionWhenSeatIsTaken() {
        LockSeatsDto dto = new LockSeatsDto();
        dto.setScreeningId(1L);
        dto.setUserId(10L);
        dto.setSeatIds(List.of(5L));

        Screening screening = new Screening();
        screening.setId(1L);

        User user = new User();

        Seat seat = new Seat();
        seat.setId(5L);
        seat.setSeatNumber(5);

        when(screeningRepository.findById(1L)).thenReturn(Optional.of(screening));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(seatRepository.findById(5L)).thenReturn(Optional.of(seat));

        when(ticketRepository.existsByScreeningIdAndSeatId(1L, 5L)).thenReturn(true);

        assertThrows(SeatAlreadyTakenException.class, () -> bookingService.lockSeats(dto));

        verify(bookingRepository, never()).save(any());
    }


    @Test
    @DisplayName("Powinien potwierdzić rezerwację i zmienić status na OPLACONA")
    void shouldConfirmBooking() {
        User user = new User();
        user.setEmail("test@test.pl");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setExpirationTime(LocalDateTime.now().plusMinutes(10));
        booking.setUser(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.confirmBooking(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.OPLACONA);
        assertThat(booking.getExpirationTime()).isNull();
        verify(bookingRepository).save(booking);
        verify(logRepository).saveLog(eq("BOOKING_CONFIRM"), anyString(), eq("test@test.pl"));
    }

    @Test
    @DisplayName("Powinien anulować rezerwację, jeśli czas na potwierdzenie minął")
    void shouldCancelBookingWhenExpired() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setExpirationTime(LocalDateTime.now().minusMinutes(1));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> bookingService.confirmBooking(1L));

        assertThat(ex.getMessage()).contains("Czas na dokończenie rezerwacji minął");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ANULOWANA);
        verify(bookingRepository).save(booking);
    }


    @Test
    @DisplayName("Powinien pozwolić użytkownikowi anulować własną rezerwację")
    void shouldCancelBookingByUser() {
        String userEmail = "jan@test.pl";
        User user = new User();
        user.setEmail(userEmail);

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

        bookingService.cancelBooking(1L, userEmail);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ANULOWANA);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Nie powinien pozwolić anulować rezerwacji mniej niż 30 min przed seansem")
    void shouldThrowExceptionWhenCancelingTooLate() {
        String userEmail = "jan@test.pl";
        User user = new User();
        user.setEmail(userEmail);

        Screening screening = new Screening();
        screening.setStartTime(LocalDateTime.now().plusMinutes(10));

        Ticket ticket = new Ticket();
        ticket.setScreening(screening);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTickets(List.of(ticket));

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.cancelBooking(1L, userEmail));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Nie powinien pozwolić anulować cudzej rezerwacji")
    void shouldThrowExceptionWhenCancelingSomeoneElseBooking() {
        User owner = new User();
        owner.setEmail("wlasciciel@test.pl");

        Booking booking = new Booking();
        booking.setUser(owner);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bookingService.cancelBooking(1L, "zlodziej@test.pl"));

        assertThat(ex.getMessage()).contains("Nie możesz anulować cudzej rezerwacji!");
    }


    @Test
    @DisplayName("Powinien zaktualizować typy biletów i przeliczyć cenę")
    void shouldUpdateTicketTypes() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setTotalAmount(25.0);

        Ticket ticket = new Ticket();
        ticket.setId(100L);
        ticket.setTicketType(TicketType.NORMALNY);
        ticket.setPrice(25.0);

        booking.setTickets(new ArrayList<>(List.of(ticket)));

        UpdateTicketTypeDto updateDto = new UpdateTicketTypeDto(100L, TicketType.ULGOWY);
        List<UpdateTicketTypeDto> updates = List.of(updateDto);

        TicketPrice ulgowyPrice = new TicketPrice(2L, TicketType.ULGOWY, 15.0);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(ticketPriceRepository.findByTicketType(TicketType.ULGOWY)).thenReturn(Optional.of(ulgowyPrice));
        when(bookingMapper.mapToDto(any())).thenReturn(new BookingDto());

        bookingService.updateTicketTypes(1L, updates);

        assertThat(ticket.getTicketType()).isEqualTo(TicketType.ULGOWY);
        assertThat(ticket.getPrice()).isEqualTo(15.0);
        assertThat(booking.getTotalAmount()).isEqualTo(15.0);
        verify(bookingRepository).save(booking);
    }
}