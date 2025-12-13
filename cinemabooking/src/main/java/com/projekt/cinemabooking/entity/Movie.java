package com.projekt.cinemabooking.entity;

import com.projekt.cinemabooking.entity.enums.MovieGenre;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 100)
    private String director;

    @Enumerated(EnumType.STRING)
    private MovieGenre genre;

    private int durationMinutes;
    private int ageRating;

    private String posterUrl;
    private String trailerUrl;
}
