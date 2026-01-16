package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.input.CreateScreeningDto;
import com.projekt.cinemabooking.dto.output.ScreeningDto;
import com.projekt.cinemabooking.entity.Screening;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "theaterRoom", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    void updateScreeningFromDto(CreateScreeningDto screeningDto, @MappingTarget Screening screening);

    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "theaterRoom.name", target = "theaterRoomName")
    @Mapping(source = "theaterRoom.id", target = "theaterRoomId")
    ScreeningDto mapToDto(Screening screening);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "theaterRoom", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    Screening mapToEntity(CreateScreeningDto dto);
}