package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.dto.admin.SalesStatsDto;
import com.projekt.cinemabooking.entity.Booking;
import com.projekt.cinemabooking.entity.Ticket;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.entity.enums.BookingStatus;
import com.projekt.cinemabooking.entity.enums.TicketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(SalesStatisticsRepository.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
class SalesStatisticsRepositoryTest {

    @Autowired
    private SalesStatisticsRepository salesRepository;

    @Autowired
    private TestEntityManager entityManager;

    private LocalDateTime now;
    private User mainUser;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        mainUser = createUser("sales@test.pl");

        createBookingWithTickets(mainUser, now, BookingStatus.OPLACONA, 50.0, 50.0);

        createBookingWithTickets(mainUser, now.minusDays(1), BookingStatus.OPLACONA, 100.0);

        createBookingWithTickets(mainUser, now, BookingStatus.ANULOWANA, 50.0);
    }

    @Test
    void shouldAggregateSalesByDate() {
        List<SalesStatsDto> stats = salesRepository.getSalesByDate("sale_date", "DESC");

        assertThat(stats).hasSize(2);

        SalesStatsDto todayStats = stats.get(0);
        assertThat(todayStats.getDate()).isEqualTo(now.toLocalDate());
        assertThat(todayStats.getTicketsSold()).isEqualTo(2);
        assertThat(todayStats.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(100.0));

        SalesStatsDto yesterdayStats = stats.get(1);
        assertThat(yesterdayStats.getDate()).isEqualTo(now.minusDays(1).toLocalDate());
        assertThat(yesterdayStats.getTicketsSold()).isEqualTo(1);
        assertThat(yesterdayStats.getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
    }

    @Test
    void shouldSortByRevenueAscending() {
        User secondUser = createUser("sales2@test.pl");

        createBookingWithTickets(secondUser, now.minusDays(2), BookingStatus.OPLACONA, 10.0);

        List<SalesStatsDto> stats = salesRepository.getSalesByDate("revenue", "ASC");

        assertThat(stats).hasSize(3);
        assertThat(stats.getFirst().getTotalRevenue()).isEqualByComparingTo(BigDecimal.valueOf(10.0));
    }


    private User createUser(String email) {
        User u = new User();
        u.setEmail(email);
        u.setPassword("pass");
        u.setFirstName("Test");
        u.setLastName("User");
        entityManager.persist(u);
        return u;
    }

    private void createBookingWithTickets(User user, LocalDateTime time, BookingStatus status, Double... prices) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingTime(time);
        booking.setStatus(status);
        entityManager.persist(booking);

        for (Double price : prices) {
            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setPrice(price);
            ticket.setTicketType(TicketType.NORMALNY);
            entityManager.persist(ticket);
        }
        entityManager.flush();
    }
}
