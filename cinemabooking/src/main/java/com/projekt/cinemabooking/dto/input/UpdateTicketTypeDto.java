package com.projekt.cinemabooking.dto.input;

import com.projekt.cinemabooking.entity.enums.TicketType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketTypeDto {

    @NotNull(message = "ID biletu jest wymagane")
    private Long ticketId;

    @NotNull(message = "Nowy typ biletu jest wymagany")
    private TicketType newType;
}