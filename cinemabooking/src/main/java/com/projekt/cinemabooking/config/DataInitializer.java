package com.projekt.cinemabooking.config;

import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.Seat;
import com.projekt.cinemabooking.entity.TheaterRoom;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.repository.RoleRepository;
import com.projekt.cinemabooking.repository.SeatRepository;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Dodajemy nowe repozytoria
    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Inicjalizacja ról i użytkowników (to już masz)
        initUsersAndRoles();

        // 2. Inicjalizacja Miejsc w Salach (TO DODAJEMY)
        generateSeatsForRooms();
    }

    private void generateSeatsForRooms() {
        // Pobieramy wszystkie sale zdefiniowane w data.sql
        List<TheaterRoom> rooms = theaterRoomRepository.findAll();

        for (TheaterRoom room : rooms) {

            System.out.println(">>> Generowanie miejsc dla sali: " + room.getName());

            // Pętla po rzędach
            for (int row = 1; row <= room.getTotalRows(); row++) {
                // Pętla po miejscach w rzędzie
                for (int number = 1; number <= room.getSeatsPerRow(); number++) {
                    Seat seat = new Seat();
                    seat.setTheaterRoom(room);
                    seat.setRowNumber(row);
                    seat.setSeatNumber(number);
                    seatRepository.save(seat);
                }
            }
        }
    }

    private void initUsersAndRoles() {
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_USER");

        if (userRepository.findByEmail("admin@cinema.pl").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@cinema.pl");
            admin.setFirstName("Jan");
            admin.setLastName("Kowalski");
            admin.setActive(true);
            admin.setPassword(passwordEncoder.encode("admin123"));

            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
            admin.setRoles(Set.of(adminRole, userRole));

            userRepository.save(admin);
            System.out.println(">>> Utworzono konto ADMINA");
        }

        if (userRepository.findByEmail("user@cinema.pl").isEmpty()) {
            User user = new User();
            user.setEmail("user@cinema.pl");
            user.setFirstName("Anna");
            user.setLastName("Nowak");
            user.setActive(true);
            user.setPassword(passwordEncoder.encode("user123"));

            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
            user.setRoles(Set.of(userRole));

            userRepository.save(user);
            System.out.println(">>> Utworzono konto USERA");
        }
    }

    private void createRoleIfNotFound(String name) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = new Role();
            role.setName(name);
            roleRepository.save(role);
        }
    }
}