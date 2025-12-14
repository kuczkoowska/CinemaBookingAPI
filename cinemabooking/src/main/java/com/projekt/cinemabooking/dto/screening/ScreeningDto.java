package com.projekt.cinemabooking.dto.screening;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreeningDto {
    private Long id;
    private LocalDateTime startTime;

    private Long movieId;
    private String movieTitle;

    private Long theaterRoomId;
    private String theaterRoomName;
}
