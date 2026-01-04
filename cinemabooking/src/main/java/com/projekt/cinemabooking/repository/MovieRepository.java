package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m WHERE " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:genre IS NULL OR m.genre = :genre)")
    Page<Movie> searchMovies(@Param("title") String title,
                             @Param("genre") MovieGenre genre,
                             Pageable pageable);
}
