package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.booking.BookingDto;
import com.projekt.cinemabooking.dto.seat.LockSeatsDto;
import com.projekt.cinemabooking.dto.ticket.UpdateTicketTypeDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.exception.SeatAlreadyTakenException;
import com.projekt.cinemabooking.mapper.BookingMapper;
import com.projekt.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final LogRepository logRepository;
    private final BookingMapper bookingMapper;
    private final TicketPriceRepository ticketPriceRepository;


    @Transactional
    public Long lockSeats(LockSeatsDto dto) {

        Screening screening = screeningRepository.findById(dto.getScreeningId()).orElseThrow(() -> new ResourceNotFoundException("Seans", dto.getScreeningId()));

        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));

        Booking booking = new Booking();
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setExpirationTime(LocalDateTime.now().plusMinutes(15));
        booking.setUser(user);
        booking.setTickets(new ArrayList<>());

        double tempTotal = 0.0;

        for (Long seatId : dto.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new ResourceNotFoundException("Miejsce", seatId));

            if (ticketRepository.existsByScreeningIdAndSeatId(screening.getId(), seatId)) {
                throw new SeatAlreadyTakenException("Miejsce " + seat.getSeatNumber() + " zajęte!");
            }

            Ticket ticket = new Ticket();
            ticket.setScreening(screening);
            ticket.setSeat(seat);
            ticket.setBooking(booking);

            ticket.setTicketType(TicketType.NORMALNY);
            double price = getPriceForType(TicketType.NORMALNY);
            ticket.setPrice(price);
            tempTotal += price;

            booking.getTickets().add(ticket);
        }

        booking.setTotalAmount(tempTotal);
        return bookingRepository.save(booking).getId();
    }

    @Transactional
    public BookingDto updateTicketTypes(Long bookingId, List<UpdateTicketTypeDto> updates) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        if (booking.getStatus() != BookingStatus.OCZEKUJE) {
            throw new IllegalStateException("Nie można edytować zatwierdzonej rezerwacji");
        }

        double newTotal = 0.0;

        for (Ticket ticket : booking.getTickets()) {
            for (UpdateTicketTypeDto update : updates) {
                if (ticket.getId().equals(update.getTicketId())) {
                    ticket.setTicketType(update.getNewType());

                    TicketType newType = update.getNewType();
                    ticket.setPrice(getPriceForType(newType));
                }
            }
            newTotal += ticket.getPrice();
        }

        booking.setTotalAmount(newTotal);
        bookingRepository.save(booking);

        return bookingMapper.mapToDto(booking);
    }

    private double getPriceForType(TicketType type) {
        return ticketPriceRepository.findByTicketType(type)
                .map(TicketPrice::getPrice)
                .orElseThrow(() -> new RuntimeException("Brak ceny w systemie dla typu: " + type));
    }

    @Transactional
    public void cancelBooking(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Nie możesz anulować cudzej rezerwacji!");
        }

        if (booking.getStatus() == BookingStatus.ANULOWANA) {
            throw new IllegalArgumentException("Ta rezerwacja jest już anulowana.");
        }

        LocalDateTime screeningStartTime = booking.getTickets().getFirst().getScreening().getStartTime();
        LocalDateTime deadline = screeningStartTime.minusMinutes(30);

        if (screeningStartTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Nie można anulować rezerwacji na seans, który już się zaczął.");
        }

        if (LocalDateTime.now().isAfter(deadline)) {
            throw new IllegalArgumentException("Zbyt późno na zmiany. Rezerwację można edytować najpóźniej 30 minut przed seansem.");
        }

        booking.setStatus(BookingStatus.ANULOWANA);
        bookingRepository.save(booking);
        logRepository.saveLog("BOOKING_CANCEL", "Anulowano rezerwację nr " + bookingId, userEmail);
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        return bookingMapper.mapToDto(booking);
    }

    @Transactional
    public void confirmBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        if (booking.getStatus() == BookingStatus.ANULOWANA) {
            throw new IllegalArgumentException("Rezerwacja wygasła lub została anulowana.");
        }

        if (booking.getExpirationTime() != null && LocalDateTime.now().isAfter(booking.getExpirationTime())) {
            booking.setStatus(BookingStatus.ANULOWANA);
            bookingRepository.save(booking);
            throw new IllegalArgumentException("Czas na dokończenie rezerwacji minął.");
        }

        booking.setStatus(BookingStatus.OPLACONA);
        booking.setExpirationTime(null);

        bookingRepository.save(booking);
        logRepository.saveLog("BOOKING_CONFIRM", "Opłacono rezerwację nr " + bookingId, booking.getUser().getEmail());
    }
}
