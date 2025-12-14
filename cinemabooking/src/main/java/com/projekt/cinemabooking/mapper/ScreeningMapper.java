package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.screening.CreateScreeningDto;
import com.projekt.cinemabooking.dto.screening.ScreeningDto;
import com.projekt.cinemabooking.entity.Screening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {

    //bez movieId i theaterRoomId -> tylko data (moze dodam is3d etc.)
    void updateScreeningFromDto(CreateScreeningDto screeningDto, @MappingTarget Screening screening);

    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "theaterRoom.name", target = "theaterRoomName")
    @Mapping(source = "theaterRoom.id", target = "theaterRoomId")
    ScreeningDto mapToDto(Screening screening);

    Screening mapToEntity(ScreeningDto dto);
}