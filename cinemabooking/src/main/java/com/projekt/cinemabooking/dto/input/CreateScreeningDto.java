package com.projekt.cinemabooking.dto.input;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScreeningDto {

    @NotNull(message = "Musisz wybrać film")
    private Long movieId;

    @NotNull(message = "Musisz wybrać salę")
    private Long theaterRoomId;

    @NotNull(message = "Data seansu jest wymagana")
    @Future(message = "Seans musi odbyć się w przyszłości")
    private LocalDateTime startTime;
}
