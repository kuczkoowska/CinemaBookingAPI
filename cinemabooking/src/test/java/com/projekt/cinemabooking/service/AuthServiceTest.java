package com.projekt.cinemabooking.service;


import com.projekt.cinemabooking.dto.input.RegisterDto;
import com.projekt.cinemabooking.entity.Role;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.repository.LogRepository;
import com.projekt.cinemabooking.repository.RoleRepository;
import com.projekt.cinemabooking.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private LogRepository logRepository;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Powinien zarejestrować użytkownika kiedy email jest unikatowy")
    void shouldRegisterUserSuccessfully() {
        RegisterDto dto = RegisterDto.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("Jan")
                .lastName("Kowalski")
                .build();

        Role role = new Role(1L, "ROLE_USER");

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPass");

        authService.registerUser(dto);

        verify(userRepository, times(1)).save(any(User.class));

        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Powinien wyrzucić błą∂ kiedy email jest zajęty")
    void shouldThrowExceptionWhenEmailIsTaken() {
        RegisterDto dto = RegisterDto.builder().email("zajety@example.com").build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        Throwable thrown = catchThrowable(() -> authService.registerUser(dto));

        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("zajęty");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Powinien wyrzucić ResourceNotFoundException kiedy nie ma ROLE_USER")
    void shouldThrowExceptionWhenRoleIsMissing() {
        RegisterDto dto = RegisterDto.builder().email("nowy@example.com").password("pass").build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> authService.registerUser(dto));

        assertThat(thrown)
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("ROLE_USER");

        verify(userRepository, never()).save(any());
    }
}
