package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.user.RegisterDto;
import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.LogRepository;
import com.projekt.cinemabooking.repository.RoleRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogRepository logRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email już jest zajęty");
        }

        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Rola ROLE_USER nie została znaleziona w bazie"));

        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .isActive(true)
                .roles(Set.of(roleUser))
                .build();

        userRepository.save(user);

        logRepository.saveLog("AUTH_REGISTER", "Zarejestrowano użytkownika", user.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Rejestracja udana! Możesz się zalogować.");
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        logRepository.saveLog("AUTH_SUCCESS", "Zalogowano poprawnie", authentication.getName());
        return ResponseEntity.ok("Zalogowano pomyślnie! Witaj " + authentication.getName());
    }
}
