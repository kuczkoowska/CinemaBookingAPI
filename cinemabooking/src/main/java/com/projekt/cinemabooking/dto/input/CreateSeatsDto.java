package com.projekt.cinemabooking.dto.input;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSeatsDto {
    @Min(value = 1, message = "Liczba rzędów musi być dodatnia")
    private int rows;

    @Min(value = 1, message = "Liczba miejsc w rzędzie musi być dodatnia")
    private int seatsPerRow;
}