package com.projekt.cinemabooking.dto.ticket;

import com.projekt.cinemabooking.entity.enums.TicketType;
import lombok.Data;

@Data
public class UpdateTicketTypeDto {
    private Long ticketId;
    private TicketType newType;
}
