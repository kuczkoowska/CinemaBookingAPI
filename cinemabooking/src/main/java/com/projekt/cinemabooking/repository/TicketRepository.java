package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByScreeningId(Long screeningId);

    @Query("SELECT COUNT(t) > 0 FROM Ticket t JOIN t.booking b " +
            "WHERE t.screening.id = :screeningId " +
            "AND t.seat.id = :seatId " +
            "AND b.status != 'ANULOWANA'")
    boolean existsByScreeningIdAndSeatId(@Param("screeningId") Long screeningId, @Param("seatId") Long seatId);
}
