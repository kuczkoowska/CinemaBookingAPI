package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.TicketPrice;
import com.projekt.cinemabooking.entity.enums.TicketType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TicketPriceRepositoryTest {

    @Autowired
    private TicketPriceRepository ticketPriceRepository;

    @Test
    @DisplayName("Powinien znaleźć cennik na podstawie typu biletu")
    void shouldFindPriceByTicketType() {
        TicketPrice ticketPrice = new TicketPrice();
        ticketPrice.setTicketType(TicketType.NORMALNY);
        ticketPrice.setPrice(25.50);
        ticketPriceRepository.save(ticketPrice);

        Optional<TicketPrice> found = ticketPriceRepository.findByTicketType(TicketType.NORMALNY);

        assertThat(found).isPresent();
        assertThat(found.get().getTicketType()).isEqualTo(TicketType.NORMALNY);
        assertThat(found.get().getPrice()).isEqualTo(25.50);
    }

    @Test
    @DisplayName("Powinien zwrócić empty, gdy nie ma ceny dla danego typu biletu")
    void shouldReturnEmptyForUnknownTicketType() {
        TicketPrice ticketPrice = new TicketPrice();
        ticketPrice.setTicketType(TicketType.NORMALNY);
        ticketPriceRepository.save(ticketPrice);

        Optional<TicketPrice> found = ticketPriceRepository.findByTicketType(TicketType.ULGOWY);

        assertThat(found).isEmpty();
    }
}