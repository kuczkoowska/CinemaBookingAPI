package com.projekt.cinemabooking.mapper;

import com.projekt.cinemabooking.dto.CreateMovieDto;
import com.projekt.cinemabooking.dto.MovieDto;
import com.projekt.cinemabooking.entity.Movie;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    void updateMovieFromDto(CreateMovieDto movieDto, @MappingTarget Movie movie);

    MovieDto mapToDto(Movie movie);

    Movie mapToEntity(CreateMovieDto dto);
}