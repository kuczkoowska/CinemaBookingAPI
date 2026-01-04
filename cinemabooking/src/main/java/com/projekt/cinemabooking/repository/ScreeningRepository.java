package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    List<Screening> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM Screening s WHERE s.theaterRoom.id = :roomId " +
            "AND s.startTime < :endTime " +
            "AND s.endTime > :startTime")
    List<Screening> findOverlappingScreenings(@Param("roomId") Long roomId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);

    boolean existsByTheaterRoomId(Long theaterRoomId);

    boolean existsByTheaterRoomIdAndStartTimeAfter(Long theaterRoomId, LocalDateTime time);
}
