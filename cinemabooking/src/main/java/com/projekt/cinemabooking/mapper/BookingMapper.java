package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.output.BookingDto;
import com.projekt.cinemabooking.entity.Booking;
import com.projekt.cinemabooking.entity.Ticket;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TicketMapper.class})
public interface BookingMapper {


    @Mapping(target = "movieTitle", ignore = true)
    @Mapping(target = "theaterRoomName", ignore = true)
    @Mapping(target = "screeningTime", ignore = true)
    BookingDto mapToDto(Booking booking);


    @AfterMapping
    default void mapScreeningDetails(Booking source, @MappingTarget BookingDto target) {
        if (source.getTickets() == null || source.getTickets().isEmpty()) {
            return;
        }


        Ticket firstTicket = source.getTickets().getFirst();
    }
}