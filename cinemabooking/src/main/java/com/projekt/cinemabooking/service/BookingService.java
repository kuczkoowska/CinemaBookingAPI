package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.booking.CreateBookingDto;
import com.projekt.cinemabooking.dto.ticket.TicketDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SalesStatisticsRepository salesStatisticsRepository;
    private final LogRepository logRepository;

    @Transactional
    public Long createBooking(CreateBookingDto createBookingDto) {

        Screening screening = screeningRepository.findById(createBookingDto.getScreeningId()).orElseThrow(() -> new ResourceNotFoundException("Seans", createBookingDto.getScreeningId()));

        User user = userRepository.findById(createBookingDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Użytkownik", createBookingDto.getUserId()));

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.OPLACONA);
        booking.setUser(user);
        booking.setTickets(new ArrayList<>());

        double totalAmount = 0.0;

        for (TicketDto ticketDto : createBookingDto.getTickets()) {
            Seat seat = seatRepository.findById(ticketDto.getSeatId()).orElseThrow(() -> new ResourceNotFoundException("Miejsce", ticketDto.getSeatId()));

            boolean isTaken = ticketRepository.existsByScreeningIdAndSeatId(screening.getId(), seat.getId());

            if (isTaken) {
                throw new RuntimeException("Miejsce numer " + seat.getSeatNumber() + " w rzędzie " + seat.getRowNumber() + " jest już zajęte!");
            }


            Ticket ticket = new Ticket();
            ticket.setTicketType(ticketDto.getTicketType());
            ticket.setScreening(screening);
            ticket.setSeat(seat);
            ticket.setBooking(booking);

            double price = calculatePrice(ticketDto.getTicketType());
            ticket.setPrice(price);

            totalAmount += price;

            booking.getTickets().add(ticket);
        }

        booking.setTotalAmount(totalAmount);
        Booking savedBooking = bookingRepository.save(booking);
        logRepository.saveLog("BOOKING", "Utworzono rezerwację nr " + savedBooking.getId(), user.getEmail());
        return savedBooking.getId();
    }

    private double calculatePrice(TicketType ticketType) {
        switch (ticketType) {
            case ULGOWY:
                return 15.00;
            case NORMALNY:
            default:
                return 25.00;
        }
    }
}
