package com.projekt.cinemabooking.repository;


import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.Screening;
import com.projekt.cinemabooking.entity.TheaterRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ScreeningRepositoryTest {

    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TheaterRoomRepository theaterRoomRepository;

    private Movie movie;
    private TheaterRoom room;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setTitle("Matrix");
        movie.setDurationMinutes(120);
        movieRepository.save(movie);

        room = new TheaterRoom();
        room.setName("Sala 1");
        theaterRoomRepository.save(room);
    }

    @Test
    void shouldFindScreeningsByMovieAndStartTimeBetween() {
        createScreening(LocalDateTime.of(2025, 1, 1, 12, 0));
        createScreening(LocalDateTime.of(2025, 1, 1, 18, 0));

        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 14, 0);

        List<Screening> result = screeningRepository.findByMovieIdAndStartTimeBetween(movie.getId(), start, end);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindOverlappingScreenings() {
        Screening existing = createScreening(LocalDateTime.of(2025, 1, 1, 14, 0));
        existing.setEndTime(existing.getStartTime().plusMinutes(movie.getDurationMinutes()));
        screeningRepository.save(existing);

        LocalDateTime newStart = LocalDateTime.of(2025, 1, 1, 15, 0);
        LocalDateTime newEnd = LocalDateTime.of(2025, 1, 1, 17, 0);

        List<Screening> overlaps = screeningRepository.findOverlappingScreenings(room.getId(), newStart, newEnd);

        assertThat(overlaps).isNotEmpty();
    }

    @Test
    void shouldReturnTrueIfScreeningExistsInRoom() {
        createScreening(LocalDateTime.now());

        boolean exists = screeningRepository.existsByTheaterRoomId(room.getId());

        assertTrue(exists);
    }

    @Test
    void shouldCheckIfScreeningExistsAfterDate() {
        LocalDateTime future = LocalDateTime.now().plusDays(2);
        createScreening(future);

        boolean exists = screeningRepository.existsByTheaterRoomIdAndStartTimeAfter(room.getId(), LocalDateTime.now().plusDays(1));

        assertTrue(exists);
    }

    @Test
    void shouldFindFutureScreeningsForMovie() {
        createScreening(LocalDateTime.now().minusHours(5));
        createScreening(LocalDateTime.now().plusHours(5));

        List<Screening> result = screeningRepository.findByMovieIdAndStartTimeAfter(movie.getId(), LocalDateTime.now());

        assertThat(result).hasSize(1);
    }

    private Screening createScreening(LocalDateTime start) {
        Screening s = new Screening();
        s.setMovie(movie);
        s.setTheaterRoom(room);
        s.setStartTime(start);
        return screeningRepository.save(s);
    }
}