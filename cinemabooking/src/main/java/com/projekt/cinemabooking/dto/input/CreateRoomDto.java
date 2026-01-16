package com.projekt.cinemabooking.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomDto {
    @NotBlank(message = "Nazwa sali jest wymagana")
    private String name;

    @Min(value = 1, message = "Liczba rzędów musi być min. 1")
    private int rows;

    @Min(value = 1, message = "Liczba miejsc w rzędzie musi być min. 1")
    private int seatsPerRow;
}