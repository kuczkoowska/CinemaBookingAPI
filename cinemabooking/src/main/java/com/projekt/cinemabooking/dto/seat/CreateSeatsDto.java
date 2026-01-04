package com.projekt.cinemabooking.dto.seat;

import lombok.Data;

@Data
public class CreateSeatsDto {
    private int rows;
    private int seatsPerRow;
}
