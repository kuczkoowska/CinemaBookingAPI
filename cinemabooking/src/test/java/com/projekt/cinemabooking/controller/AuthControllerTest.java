package com.projekt.cinemabooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.controller.api.AuthController;
import com.projekt.cinemabooking.dto.input.RegisterDto;
import com.projekt.cinemabooking.exception.GlobalExceptionHandler;
import com.projekt.cinemabooking.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, AuthControllerTest.TestConfig.class})
class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;


    @Test
    void shouldReturn201_WhenRegistrationIsSuccessful() throws Exception {
        RegisterDto dto = RegisterDto.builder()
                .email("nowy@test.pl")
                .password("haslo123")
                .firstName("Test")
                .lastName("User")
                .build();

        doNothing().when(authService).registerUser(any(RegisterDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Rejestracja udana! Możesz się zalogować."));
    }

    @Test
    void shouldReturn400_WhenValidationFails() throws Exception {
        RegisterDto dto = RegisterDto.builder()
                .email("zly-email")
                .password("")
                .firstName("")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Błąd walidacji danych"));
    }

    @Test
    void shouldReturn409_WhenEmailIsTaken() throws Exception {
        RegisterDto dto = RegisterDto.builder()
                .email("zajety@test.pl")
                .password("haslo123")
                .firstName("Jan")
                .lastName("Kowalski")
                .build();

        doThrow(new IllegalArgumentException("Email jest już zajęty."))
                .when(authService).registerUser(any(RegisterDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email jest już zajęty."));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        AuthService authService() {
            return Mockito.mock(AuthService.class);
        }
    }
}
