package com.projekt.cinemabooking.entity;

import com.projekt.cinemabooking.entity.enums.MovieGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Tytuł jest wymagany")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Opis jest wymagany")
    @Column(nullable = false, length = 2000)
    private String description;

    @NotBlank(message = "Reżyser jest wymagany")
    @Column(nullable = false, length = 100)
    private String director;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieGenre genre;

    @Min(value = 1, message = "Film musi trwać min 1 minutę")
    @Column(nullable = false)
    private int durationMinutes;

    private int ageRating;

    @NotNull
    @Column(nullable = false)
    private String posterUrl;

    private String trailerUrl;
}