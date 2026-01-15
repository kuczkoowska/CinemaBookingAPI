package com.projekt.cinemabooking.dto.ticket;

import com.projekt.cinemabooking.entity.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateTicketTypeDto {
    private Long ticketId;
    private TicketType newType;
}
