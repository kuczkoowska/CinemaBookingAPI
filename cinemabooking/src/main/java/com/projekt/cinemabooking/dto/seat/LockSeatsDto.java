package com.projekt.cinemabooking.dto.seat;

import lombok.Data;

import java.util.List;

@Data
public class LockSeatsDto {
    private Long screeningId;
    private List<Long> seatIds;
}
