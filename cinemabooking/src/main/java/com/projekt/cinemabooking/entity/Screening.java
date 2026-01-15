package com.projekt.cinemabooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "screenings")
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Czas rozpoczęcia jest wymagany")
    @Future(message = "Seans nie może odbyć się w przeszłości")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @NotNull(message = "Film jest wymagany")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_room_id", nullable = false)
    @NotNull(message = "Sala jest wymagana")
    private TheaterRoom theaterRoom;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @PrePersist
    @PreUpdate
    public void calculateEndTime() {
        if (movie != null && startTime != null) {
            this.endTime = startTime.plusMinutes(movie.getDurationMinutes() + 20);
        }
    }
}