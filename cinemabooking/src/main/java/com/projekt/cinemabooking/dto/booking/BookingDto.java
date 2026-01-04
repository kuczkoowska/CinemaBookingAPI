package com.projekt.cinemabooking.dto.booking;

import com.projekt.cinemabooking.dto.ticket.TicketDto;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {

    private Long id;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private double totalAmount;
    
    private String movieTitle;
    private String theaterRoomName;
    private LocalDateTime screeningTime;

    private List<TicketDto> tickets;
}
