package com.projekt.cinemabooking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {
    private Long id;
    private String title;
    private String description;
    private String director;
    private String posterUrl;
    private String trailerUrl;
    private String genre;
    private int durationMinutes;
    private int ageRating;
}
