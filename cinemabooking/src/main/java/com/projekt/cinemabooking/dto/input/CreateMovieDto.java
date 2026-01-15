package com.projekt.cinemabooking.dto.input;

import com.projekt.cinemabooking.entity.enums.MovieGenre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovieDto {

    @NotBlank(message = "Tytuł nie może być pusty")
    @Size(min = 2, max = 150, message = "Tytuł musi mieć od 2 do 150 znaków")
    private String title;

    @NotNull(message = "Gatunek jest wymagany")
    private MovieGenre genre;

    @NotBlank(message = "Opis jest wymagany")
    @Size(max = 2000, message = "Opis jest za długi")
    private String description;

    @NotBlank(message = "Reżyser jest wymagany")
    private String director;

    @Min(value = 1, message = "Film musi trwać przynajmniej minutę")
    private int durationMinutes;


    @Min(value = 0, message = "Wiek nie może być ujemny")
    private int ageRating;

    @NotBlank(message = "Link do plakatu jest wymagany")
    private String posterUrl;

    private String trailerUrl;
}
