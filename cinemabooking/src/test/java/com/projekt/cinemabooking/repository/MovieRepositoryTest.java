package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        createMovie("Avatar", MovieGenre.SCI_FI);
        createMovie("Avengers", MovieGenre.AKCJA);
        createMovie("Titanic", MovieGenre.DRAMAT);
        createMovie("Alien", MovieGenre.SCI_FI);
    }

    @Test
    void shouldSearchByTitleFragment() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Movie> result = movieRepository.searchMovies("Av", null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Movie::getTitle)
                .containsExactlyInAnyOrder("Avatar", "Avengers");
    }

    @Test
    void shouldSearchByGenre() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Movie> result = movieRepository.searchMovies(null, MovieGenre.SCI_FI, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Movie::getTitle)
                .contains("Avatar", "Alien");
    }

    @Test
    void shouldSearchByTitleAndGenre() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Movie> result = movieRepository.searchMovies("Al", MovieGenre.SCI_FI, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Alien");
    }

    @Test
    void shouldReturnAllWhenParamsAreNull() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Movie> result = movieRepository.searchMovies(null, null, pageable);

        assertThat(result.getContent()).hasSize(4);
    }

    private void createMovie(String title, MovieGenre genre) {
        Movie m = new Movie();
        m.setTitle(title);
        m.setGenre(genre);
        m.setDurationMinutes(120);

        m.setDirector("Test Director");
        m.setDescription("Test Description for " + title);
        m.setPosterUrl("http://example.com/poster.jpg");

        movieRepository.save(m);
    }
}