package com.projekt.cinemabooking.entity;

import com.projekt.cinemabooking.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @PrePersist
    public void prePersist() {
        if (this.bookingTime == null) this.bookingTime = LocalDateTime.now();
        if (this.status == null) this.status = BookingStatus.OCZEKUJE;
        if (this.expirationTime == null) this.expirationTime = this.bookingTime.plusMinutes(15);
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setBooking(this);
    }
}
