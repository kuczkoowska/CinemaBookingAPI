package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.*;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import com.projekt.cinemabooking.entity.enums.TicketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ScreeningRepository screeningRepository;
    @Autowired
    private TheaterRoomRepository theaterRoomRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;

    private Seat savedSeat1;
    private Seat savedSeat2;
    private Screening screeningA;
    private Screening screeningB;
    private Booking activeBooking;
    private Booking canceledBooking;

    @BeforeEach
    void setUp() {
        TheaterRoom room = new TheaterRoom();
        room.setName("Sala Testowa");
        theaterRoomRepository.save(room);

        savedSeat1 = createAndSaveSeat(room, 1, 5);
        savedSeat2 = createAndSaveSeat(room, 1, 6);

        Movie movie = new Movie();
        movie.setTitle("Testowy Film");
        movie.setDurationMinutes(120);
        movie.setDescription("Opis");
        movie.setGenre(MovieGenre.KOMEDIA);
        movieRepository.save(movie);

        screeningA = createAndSaveScreening(movie, room, LocalDateTime.now().plusHours(2));
        screeningB = createAndSaveScreening(movie, room, LocalDateTime.now().plusHours(5));

        User user = new User();
        user.setEmail("test@test.pl");
        user.setPassword("pass");
        user.setFirstName("Jan");
        user.setLastName("Testowy");
        userRepository.save(user);

        activeBooking = createAndSaveBooking(user, BookingStatus.OPLACONA);
        canceledBooking = createAndSaveBooking(user, BookingStatus.ANULOWANA);
    }


    @Test
    @DisplayName("Powinien zwrócić TRUE, gdy bilet istnieje i rezerwacja jest opłacona")
    void shouldReturnTrueWhenSeatIsTakenAndPaid() {
        createAndSaveTicket(screeningA, savedSeat1, activeBooking);

        boolean exists = ticketRepository.existsByScreeningIdAndSeatId(screeningA.getId(), savedSeat1.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Powinien zwrócić FALSE, gdy miejsce jest wolne (brak biletu)")
    void shouldReturnFalseWhenSeatIsFree() {
        boolean exists = ticketRepository.existsByScreeningIdAndSeatId(screeningA.getId(), savedSeat1.getId());

        assertFalse(exists);
    }

    @Test
    @DisplayName("Powinien zwrócić FALSE, gdy bilet istnieje, ale rezerwacja jest ANULOWANA")
    void shouldReturnFalseWhenStatusIsCanceled() {
        createAndSaveTicket(screeningA, savedSeat1, canceledBooking);

        boolean exists = ticketRepository.existsByScreeningIdAndSeatId(screeningA.getId(), savedSeat1.getId());

        assertFalse(exists, "Miejsce powinno być widoczne jako wolne jeśli rezerwacja została anulowana");
    }

    @Test
    @DisplayName("Powinien zwrócić TRUE, gdy status rezerwacji to OCZEKUJĄCA (lub inny nie-anulowany)")
    void shouldReturnTrueWhenStatusIsPending() {
        Booking pendingBooking = createAndSaveBooking(activeBooking.getUser(), BookingStatus.OCZEKUJE);
        createAndSaveTicket(screeningA, savedSeat1, pendingBooking);

        boolean exists = ticketRepository.existsByScreeningIdAndSeatId(screeningA.getId(), savedSeat1.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Powinien zwrócić FALSE, gdy bilet jest na to samo miejsce, ale inny seans")
    void shouldReturnFalseWhenCheckingSameSeatButDifferentScreening() {
        createAndSaveTicket(screeningB, savedSeat1, activeBooking);

        boolean exists = ticketRepository.existsByScreeningIdAndSeatId(screeningA.getId(), savedSeat1.getId());

        assertFalse(exists, "Miejsce na seansie A powinno być wolne, mimo że jest zajęte na seansie B");
    }

    @Test
    @DisplayName("Powinien zwrócić FALSE, gdy bilet jest na ten sam seans, ale inne miejsce")
    void shouldReturnFalseWhenCheckingDifferentSeatInSameScreening() {
        createAndSaveTicket(screeningA, savedSeat2, activeBooking);

        boolean exists = ticketRepository.existsByScreeningIdAndSeatId(screeningA.getId(), savedSeat1.getId());

        assertFalse(exists);
    }


    @Test
    @DisplayName("Powinien zwrócić listę wszystkich biletów dla danego seansu")
    void shouldFindAllByScreeningId() {
        createAndSaveTicket(screeningA, savedSeat1, activeBooking);
        createAndSaveTicket(screeningA, savedSeat2, activeBooking);

        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningA.getId());

        assertThat(tickets).hasSize(2);
        assertThat(tickets).extracting(Ticket::getScreening).containsOnly(screeningA);
    }

    @Test
    @DisplayName("Powinien zwrócić pustą listę, jeśli nie ma biletów na dany seans")
    void shouldReturnEmptyListWhenNoTicketsForScreening() {
        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningA.getId());

        assertThat(tickets).isEmpty();
    }

    @Test
    @DisplayName("Nie powinien zwracać biletów z innego seansu")
    void shouldNotReturnTicketsFromOtherScreenings() {
        createAndSaveTicket(screeningA, savedSeat1, activeBooking);
        createAndSaveTicket(screeningB, savedSeat1, activeBooking);

        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningA.getId());

        assertThat(tickets).hasSize(1);
        assertThat(tickets.getFirst().getScreening().getId()).isEqualTo(screeningA.getId());
    }

    @Test
    @DisplayName("Powinien zwrócić również bilety z ANULOWANYCH rezerwacji")
    void shouldReturnTicketsEvenIfBookingIsCanceled() {
        createAndSaveTicket(screeningA, savedSeat1, canceledBooking);

        List<Ticket> tickets = ticketRepository.findAllByScreeningId(screeningA.getId());

        assertThat(tickets).hasSize(1);
        assertThat(tickets.getFirst().getBooking().getStatus()).isEqualTo(BookingStatus.ANULOWANA);
    }


    private Seat createAndSaveSeat(TheaterRoom room, int row, int number) {
        Seat seat = new Seat();
        seat.setRowNumber(row);
        seat.setSeatNumber(number);
        seat.setTheaterRoom(room);
        return seatRepository.save(seat);
    }

    private Screening createAndSaveScreening(Movie movie, TheaterRoom room, LocalDateTime time) {
        Screening screening = new Screening();
        screening.setMovie(movie);
        screening.setTheaterRoom(room);
        screening.setStartTime(time);
        return screeningRepository.save(screening);
    }

    private Booking createAndSaveBooking(User user, BookingStatus status) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    private void createAndSaveTicket(Screening screening, Seat seat, Booking booking) {
        Ticket ticket = new Ticket();
        ticket.setTicketType(TicketType.NORMALNY);
        ticket.setPrice(25.0);
        ticket.setSeat(seat);
        ticket.setScreening(screening);
        ticket.setBooking(booking);
        ticketRepository.save(ticket);
    }
}