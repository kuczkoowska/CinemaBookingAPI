package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.output.BookingDto;
import com.projekt.cinemabooking.entity.Booking;
import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.Screening;
import com.projekt.cinemabooking.entity.Ticket;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {TicketMapper.class})
public interface BookingMapper {


    @Mapping(target = "movieTitle", ignore = true)
    @Mapping(target = "moviePosterUrl", ignore = true)
    @Mapping(target = "movieDescription", ignore = true)
    @Mapping(target = "movieDurationMinutes", ignore = true)
    @Mapping(target = "movieGenre", ignore = true)
    @Mapping(target = "movieAgeRating", ignore = true)
    @Mapping(target = "theaterRoomName", ignore = true)
    @Mapping(target = "screeningTime", ignore = true)
    BookingDto mapToDto(Booking booking);


    @AfterMapping
    default void mapScreeningDetails(Booking source, @MappingTarget BookingDto target) {
        if (source.getTickets() == null || source.getTickets().isEmpty()) {
            return;
        }

        Ticket firstTicket = source.getTickets().getFirst();
        Screening screening = firstTicket.getScreening();
        
        if (screening != null) {
            target.setScreeningTime(screening.getStartTime());
            
            if (screening.getTheaterRoom() != null) {
                target.setTheaterRoomName(screening.getTheaterRoom().getName());
            }
            
            Movie movie = screening.getMovie();
            if (movie != null) {
                target.setMovieTitle(movie.getTitle());
                target.setMoviePosterUrl(movie.getPosterUrl());
                target.setMovieDescription(movie.getDescription());
                target.setMovieDurationMinutes(movie.getDurationMinutes());
                target.setMovieGenre(movie.getGenre() != null ? movie.getGenre().name() : null);
                target.setMovieAgeRating(String.valueOf(movie.getAgeRating()));
            }
        }
    }
}