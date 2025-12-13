package com.projekt.cinemabooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
// generuje automatycznie metody get i set dla wszystkich pól w tej klasie
@NoArgsConstructor // generuje pusty konstruktor
@AllArgsConstructor // generuje konstruktor z wszystkimi polami
@Builder // zamiast new Role(null, "ADMIN")
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100)
    private String name;
}
