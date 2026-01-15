package com.projekt.cinemabooking.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private int rowNumber;
    private int seatNumber;
    private boolean available;
}
