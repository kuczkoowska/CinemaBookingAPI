package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(MovieGenre genre);

    List<Movie> findByTitleContainingIgnoreCase(String title);
}
