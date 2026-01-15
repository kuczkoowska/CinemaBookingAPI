package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.booking.BookingDto;
import com.projekt.cinemabooking.entity.Booking;
import com.projekt.cinemabooking.entity.Screening;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final TicketMapper ticketMapper;

    public BookingDto mapToDto(Booking booking) {
        String movieTitle = "Nieznany";
        String roomName = "Nieznana";
        LocalDateTime screeningTime = null;

        if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
            Screening screening = booking.getTickets().getFirst().getScreening();
            movieTitle = screening.getMovie().getTitle();
            roomName = screening.getTheaterRoom().getName();
            screeningTime = screening.getStartTime();
        }

        return BookingDto.builder()
                .id(booking.getId())
                .bookingTime(booking.getBookingTime())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .movieTitle(movieTitle)
                .theaterRoomName(roomName)
                .screeningTime(screeningTime)
                .expirationTime(booking.getExpirationTime())
                .tickets(booking.getTickets() != null
                        ? booking.getTickets().stream()
                        .map(ticketMapper::mapToDto)
                        .toList()
                        : java.util.List.of())
                .build();
    }

}
