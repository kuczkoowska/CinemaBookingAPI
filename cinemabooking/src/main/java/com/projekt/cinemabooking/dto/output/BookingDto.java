package com.projekt.cinemabooking.dto.output;

import com.projekt.cinemabooking.entity.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
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
    private BigDecimal totalAmount;

    private String movieTitle;
    private String theaterRoomName;
    private LocalDateTime screeningTime;

    private List<TicketDto> tickets;
    private LocalDateTime expirationTime;
}
