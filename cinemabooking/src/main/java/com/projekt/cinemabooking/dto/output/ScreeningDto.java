package com.projekt.cinemabooking.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningDto {
    private Long id;
    private LocalDateTime startTime;

    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private String movieDescription;
    private Integer movieDurationMinutes;
    private String movieGenre;
    private Integer movieAgeRating;

    private Long theaterRoomId;
    private String theaterRoomName;

    private Integer availableSeats;
    private Integer totalSeats;
}
