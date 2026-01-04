package com.projekt.cinemabooking.dto.ticket;

import com.projekt.cinemabooking.entity.enums.TicketType;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTicketDto {
    @NotNull(message = "Musisz wybrać miejsce")
    private Long seatId;

    @NotNull(message = "Musisz wybrać typ biletu")
    private TicketType ticketType;
}
