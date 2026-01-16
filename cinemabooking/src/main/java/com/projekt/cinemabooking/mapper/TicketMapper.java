package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.output.TicketDto;
import com.projekt.cinemabooking.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "seat.rowNumber", target = "row")
    @Mapping(source = "seat.seatNumber", target = "seatNumber")
    @Mapping(source = "seat.id", target = "seatId")
    @Mapping(source = "ticketType", target = "type")
    TicketDto mapToDto(Ticket ticket);
}
