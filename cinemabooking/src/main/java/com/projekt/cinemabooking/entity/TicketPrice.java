package com.projekt.cinemabooking.entity;

import com.projekt.cinemabooking.entity.enums.TicketType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ticket_prices")
public class TicketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Typ biletu jest wymagany")
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private TicketType ticketType;

    @NotNull(message = "Cena jest wymagana")
    @Min(value = 0, message = "Cena nie może być ujemna")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}