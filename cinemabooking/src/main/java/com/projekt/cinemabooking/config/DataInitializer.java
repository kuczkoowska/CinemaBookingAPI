package com.projekt.cinemabooking.config;


import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import com.projekt.cinemabooking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {

        if (movieRepository.count() == 0) {
            Movie m1 = Movie.builder()
                    .title("Incepcja")
                    .genre(MovieGenre.SCI_FI)
                    .description("Czasy, gdy technologia pozwala na wchodzenie w sny...")
                    .director("Christopher Nolan")
                    .durationMinutes(148)
                    .ageRating(12)
                    .build();

            Movie m2 = Movie.builder()
                    .title("Król Lew")
                    .genre(MovieGenre.ANIMACJA)
                    .description("Simba, młody lwiątko, musi odzyskać królestwo.")
                    .director("Roger Allers")
                    .durationMinutes(88)
                    .ageRating(0)
                    .build();

            movieRepository.save(m1);
            movieRepository.save(m2);

            System.out.println("Dodano przykładowe filmy do bazy danych!");
        }
    }
}