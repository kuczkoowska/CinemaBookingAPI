package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.LockSeatsDto;
import com.projekt.cinemabooking.dto.input.UpdateTicketTypeDto;
import com.projekt.cinemabooking.dto.output.BookingDto;
import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.entity.enums.LogType;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.exception.SeatAlreadyTakenException;
import com.projekt.cinemabooking.mapper.BookingMapper;
import com.projekt.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        BigDecimal defaultPrice = getPriceForType(TicketType.NORMALNY);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTickets(new ArrayList<>());
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.OCZEKUJE);
        booking.setExpirationTime(LocalDateTime.now().plusMinutes(15));

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Long seatId : dto.getSeatIds()) {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Miejsce", seatId));

            boolean isTaken = ticketRepository.existsByScreeningIdAndSeatId(
                    screening.getId(),
                    seatId,
                    List.of(BookingStatus.OPLACONA, BookingStatus.OCZEKUJE)
            );

            if (isTaken) {
                throw new SeatAlreadyTakenException("Miejsce " + seat.getSeatNumber() + " jest już zajęte!");
            }

            Ticket ticket = new Ticket();
            ticket.setScreening(screening);
            ticket.setSeat(seat);
            ticket.setBooking(booking);
            ticket.setTicketType(TicketType.NORMALNY);
            ticket.setPrice(defaultPrice);

            totalAmount = totalAmount.add(defaultPrice);

            booking.getTickets().add(ticket);
        }

        booking.setTotalAmount(totalAmount);

        try {
            Booking savedBooking = bookingRepository.save(booking);
            return bookingMapper.mapToDto(savedBooking);

        } catch (DataIntegrityViolationException e) {
            throw new SeatAlreadyTakenException("Ktoś właśnie zajął jedno z wybranych miejsc. Spróbuj ponownie.");
        }
    }

    @Transactional
    public BookingDto updateTicketTypes(Long bookingId, List<UpdateTicketTypeDto> updates, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        validateBookingOwnership(booking, userId);

        if (booking.getStatus() != BookingStatus.OCZEKUJE) {
            throw new IllegalStateException("Można edytować tylko rezerwacje w statusie OCZEKUJE.");
        }

        Map<TicketType, BigDecimal> priceMap = ticketPriceRepository.findAll().stream()
                .collect(Collectors.toMap(TicketPrice::getTicketType, TicketPrice::getPrice));

        Map<Long, TicketType> updatesMap = updates.stream()
                .collect(Collectors.toMap(UpdateTicketTypeDto::getTicketId, UpdateTicketTypeDto::getNewType));

        BigDecimal newTotal = BigDecimal.ZERO;

        for (Ticket ticket : booking.getTickets()) {
            if (updatesMap.containsKey(ticket.getId())) {
                TicketType newType = updatesMap.get(ticket.getId());

                if (!priceMap.containsKey(newType)) {
                    throw new ResourceNotFoundException("Cena dla typu " + newType + " nie jest zdefiniowana.");
                }

                ticket.setTicketType(newType);
                ticket.setPrice(priceMap.get(newType));
            }
            newTotal = newTotal.add(ticket.getPrice());
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
            throw new IllegalArgumentException("Rezerwacja jest już anulowana.");
        }

        if (booking.getTickets().isEmpty()) {
            booking.setStatus(BookingStatus.ANULOWANA);
            return;
        }

        LocalDateTime screeningStartTime = booking.getTickets().get(0).getScreening().getStartTime();

        if (LocalDateTime.now().plusMinutes(30).isAfter(screeningStartTime)) {
            throw new IllegalArgumentException("Zbyt późno na anulowanie. Wymagane 30 min przed seansem.");
        }

        booking.setStatus(BookingStatus.ANULOWANA);
        bookingRepository.save(booking);

        logRepository.saveLog(LogType.INFO, "Anulowano rezerwację nr " + bookingId, booking.getUser().getEmail());
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        validateBookingOwnership(booking, userId);
        return bookingMapper.mapToDto(booking);
    }

    @Transactional
    public void confirmBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja", bookingId));

        validateBookingOwnership(booking, userId);

        if (booking.getStatus() == BookingStatus.ANULOWANA) {
            throw new IllegalStateException("Rezerwacja została anulowana.");
        }
        if (booking.getStatus() == BookingStatus.OPLACONA) {
            return;
        }

        if (booking.getExpirationTime() != null && LocalDateTime.now().isAfter(booking.getExpirationTime())) {
            booking.setStatus(BookingStatus.ANULOWANA);
            bookingRepository.save(booking);
            throw new IllegalStateException("Czas na płatność minął. Rezerwacja wygasła.");
        }

        booking.setStatus(BookingStatus.OPLACONA);
        booking.setExpirationTime(null);

        bookingRepository.save(booking);
        logRepository.saveLog(LogType.INFO, "Opłacono rezerwację nr " + bookingId, booking.getUser().getEmail());
    }

    private BigDecimal getPriceForType(TicketType type) {
        return ticketPriceRepository.findByTicketType(type)
                .map(TicketPrice::getPrice)
                .orElseThrow(() -> new RuntimeException("Brak zdefiniowanej ceny w systemie dla typu: " + type));
    }
}