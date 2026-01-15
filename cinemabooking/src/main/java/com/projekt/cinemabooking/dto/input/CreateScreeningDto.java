package com.projekt.cinemabooking.dto.input;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScreeningDto {

    @NotNull(message = "Musisz wybrać film")
    private Long movieId;

    @NotNull(message = "Musisz wybrać salę")
    private Long theaterRoomId;

    @NotNull(message = "Data seansu jest wymagana")
    @Future(message = "Seans musi odbyć się w przyszłości")
    private LocalDateTime startTime;
}
