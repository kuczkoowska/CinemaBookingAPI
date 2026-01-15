package com.projekt.cinemabooking.dto.output;

import lombok.*;

import java.math.BigDecimal;

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
    private BigDecimal price;
}
