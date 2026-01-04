package com.projekt.cinemabooking.dto.booking;

import com.projekt.cinemabooking.dto.ticket.CreateTicketDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingDto {

    @NotNull(message = "ID seansu jest wymagane")
    private Long screeningId;

    @NotNull(message = "ID użytkownika jest wymagane")
    private Long userId;

    @NotEmpty(message = "Lista biletów nie może być pusta")
    private List<CreateTicketDto> tickets;
}
