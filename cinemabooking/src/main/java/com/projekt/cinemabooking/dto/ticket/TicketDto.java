package com.projekt.cinemabooking.dto.ticket;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDto {

    private Long id;
    private Long seatId;
    private int row;
    private int seatNumber;
    private String type;
    private double price;
}
