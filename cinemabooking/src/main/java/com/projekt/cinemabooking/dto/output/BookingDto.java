package com.projekt.cinemabooking.dto.output;

import com.projekt.cinemabooking.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private BigDecimal totalAmount;

    private String movieTitle;
    private String moviePosterUrl;
    private String movieDescription;
    private Integer movieDurationMinutes;
    private String movieGenre;
    private String movieAgeRating;
    
    private String theaterRoomName;
    private LocalDateTime screeningTime;

    private List<TicketDto> tickets;
    private LocalDateTime expirationTime;
}
