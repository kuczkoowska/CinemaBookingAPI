package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.input.CreateMovieDto;
import com.projekt.cinemabooking.dto.output.MovieDto;
import com.projekt.cinemabooking.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "id", ignore = true)
    void updateMovieFromDto(CreateMovieDto movieDto, @MappingTarget Movie movie);

    MovieDto mapToDto(Movie movie);

    @Mapping(target = "id", ignore = true)
    Movie mapToEntity(CreateMovieDto dto);
}