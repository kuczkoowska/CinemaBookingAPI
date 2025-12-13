package com.projekt.cinemabooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "theater_rooms")
public class TheaterRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int totalRows;
    private int seatsPerRow;

    @OneToMany(mappedBy = "theaterRoom", cascade = CascadeType.ALL)
    private List<Seat> seats;
}
