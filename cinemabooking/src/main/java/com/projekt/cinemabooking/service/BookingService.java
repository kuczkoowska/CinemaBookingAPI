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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private void validateBookingOwnership(Booking booking, Long currentUserId) {
        if (!booking.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Nie masz uprawnień do tej rezerwacji.");
        }
    }

    @Transactional
    public BookingDto lockSeats(LockSeatsDto dto, Long userId) {
        Screening screening = screeningRepository.findById(dto.getScreeningId())
                .orElseThrow(() -> new ResourceNotFoundException("Seans", dto.getScreeningId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTickets(new ArrayList<>());

        double tempTotal = 0.0;

        for (Long seatId : dto.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Miejsce", seatId));

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
        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.mapToDto(savedBooking);
    }

    @Transactional
    public BookingDto updateTicketTypes(Long bookingId, List<UpdateTicketTypeDto> updates, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        validateBookingOwnership(booking, userId);

        if (booking.getStatus() != BookingStatus.OCZEKUJE) {
            throw new IllegalStateException("Nie można edytować zatwierdzonej lub anulowanej rezerwacji");
        }

        Map<TicketType, Double> priceMap = ticketPriceRepository.findAll().stream()
                .collect(Collectors.toMap(TicketPrice::getTicketType, TicketPrice::getPrice));

        Map<Long, TicketType> updatesMap = updates.stream()
                .collect(Collectors.toMap(UpdateTicketTypeDto::getTicketId, UpdateTicketTypeDto::getNewType));

        double newTotal = 0.0;

        for (Ticket ticket : booking.getTickets()) {
            if (updatesMap.containsKey(ticket.getId())) {
                TicketType newType = updatesMap.get(ticket.getId());

                if (!priceMap.containsKey(newType)) {
                    throw new RuntimeException("Brak zdefiniowanej ceny dla typu: " + newType);
                }

                ticket.setTicketType(newType);
                ticket.setPrice(priceMap.get(newType));
            }
            newTotal += ticket.getPrice();
        }

        booking.setTotalAmount(newTotal);
        bookingRepository.save(booking);

        return bookingMapper.mapToDto(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        validateBookingOwnership(booking, userId);

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

        logRepository.saveLog("BOOKING_CANCEL", "Anulowano rezerwację nr " + bookingId, booking.getUser().getEmail());
    }

    @Transactional(readOnly = true) // Dodano userId
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        // 1. Walidacja właściciela
        validateBookingOwnership(booking, userId);

        return bookingMapper.mapToDto(booking);
    }

    @Transactional
    public void confirmBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        validateBookingOwnership(booking, userId);

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

    private double getPriceForType(TicketType type) {
        return ticketPriceRepository.findByTicketType(type)
                .map(TicketPrice::getPrice)
                .orElseThrow(() -> new RuntimeException("Brak ceny w systemie dla typu: " + type));
    }
}