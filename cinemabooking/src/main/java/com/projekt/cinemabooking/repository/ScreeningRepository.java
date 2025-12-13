package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByMovieId(Long movieId);

    List<Screening> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
