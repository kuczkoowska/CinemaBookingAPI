package com.projekt.cinemabooking.config;


import com.projekt.cinemabooking.entity.Movie;
import com.projekt.cinemabooking.entity.Seat;
import com.projekt.cinemabooking.entity.TheaterRoom;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import com.projekt.cinemabooking.repository.MovieRepository;
import com.projekt.cinemabooking.repository.SeatRepository;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

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

        if (theaterRoomRepository.count() == 0) {
            TheaterRoom room = new TheaterRoom();
            room.setName("Sala 1 - Główna");
            room.setTotalRows(10);
            room.setSeatsPerRow(10);

            TheaterRoom savedRoom = theaterRoomRepository.save(room);

            for (int row = 1; row <= 10; row++) {
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    Seat seat = new Seat();
                    seat.setRowNumber(row);
                    seat.setSeatNumber(seatNum);
                    seat.setTheaterRoom(savedRoom);

                    seatRepository.save(seat);
                }
            }
            System.out.println("Dodano sale do bazy danych!");
        }

        if (userRepository.count() == 0) {
            User user = User.builder()
                    .email("klient@kino.pl")
                    .password("haslo123")
                    .firstName("Jan")
                    .lastName("Kowalski")
                    .isActive(true)
                    .build();

            userRepository.save(user);
            System.out.println("Dodano pierwszego użytkownika do bazy danych!");
        }
    }
}