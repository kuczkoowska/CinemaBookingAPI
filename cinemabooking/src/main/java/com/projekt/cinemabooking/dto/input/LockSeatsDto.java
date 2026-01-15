package com.projekt.cinemabooking.dto.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockSeatsDto {
    @NotNull(message = "Musisz podać ID seansu")
    private Long screeningId;

    @NotEmpty(message = "Musisz wybrać przynajmniej jedno miejsce")
    private List<Long> seatIds;
}