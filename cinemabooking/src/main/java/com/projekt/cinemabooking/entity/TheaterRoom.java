package com.projekt.cinemabooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "theater_rooms")
public class TheaterRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nazwa sali jest wymagana")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Min(value = 1, message = "Sala musi mieć przynajmniej 1 rząd")
    @Column(nullable = false)
    private int totalRows;

    @Min(value = 1, message = "W rzędzie musi być przynajmniej 1 miejsce")
    @Column(nullable = false)
    private int seatsPerRow;

    @OneToMany(mappedBy = "theaterRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Seat> seats = new ArrayList<>();
    
    public void addSeat(Seat seat) {
        seats.add(seat);
        seat.setTheaterRoom(this);
    }
}