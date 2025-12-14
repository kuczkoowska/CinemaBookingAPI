package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.screening.CreateScreeningDto;
import com.projekt.cinemabooking.dto.screening.ScreeningDto;
import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.Screening;
import com.projekt.cinemabooking.entity.TheaterRoom;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.mapper.ScreeningMapper;
import com.projekt.cinemabooking.repository.MovieRepository;
import com.projekt.cinemabooking.repository.ScreeningRepository;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final TheaterRoomRepository theaterRoomRepository;
    private final ScreeningMapper screeningMapper;

    @Transactional
    public Long createScreening(CreateScreeningDto createScreeningDto) {
        Movie movie = movieRepository.findById(createScreeningDto.getMovieId()).orElseThrow(() -> new ResourceNotFoundException(createScreeningDto.getMovieId()));

        TheaterRoom room = theaterRoomRepository.findById(createScreeningDto.getTheaterRoomId()).orElseThrow(() -> new ResourceNotFoundException(createScreeningDto.getTheaterRoomId()));

        Screening screening = new Screening();
        screeningMapper.updateScreeningFromDto(createScreeningDto, screening);
        screening.setMovie(movie);
        screening.setTheaterRoom(room);

        return screeningRepository.save(screening).getId();
    }

    @Transactional(readOnly = true)
    public List<ScreeningDto> getAllScreenings() {
        return screeningRepository.findAll().stream()
                .map(screeningMapper::mapToDto)
                .toList();
    }
}
