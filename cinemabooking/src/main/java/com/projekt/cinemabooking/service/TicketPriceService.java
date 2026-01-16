package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.entity.TicketPrice;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.TicketPriceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketPriceService {

    private final TicketPriceRepository ticketPriceRepository;

    public List<TicketPrice> getAllPrices() {
        return ticketPriceRepository.findAll();
    }

    @Transactional
    public TicketPrice updatePrice(TicketType type, BigDecimal newPrice) {
        TicketPrice ticketPrice = ticketPriceRepository.findByTicketType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Cennik dla typu: " + type + " nie istnieje"));

        ticketPrice.setPrice(newPrice);
        return ticketPriceRepository.save(ticketPrice);
    }
}
