package com.projekt.cinemabooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.controller.api.ScreeningController;
import com.projekt.cinemabooking.dto.input.CreateScreeningDto;
import com.projekt.cinemabooking.dto.output.SeatDto;
import com.projekt.cinemabooking.service.ScreeningService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScreeningController.class)
class ScreeningControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ScreeningService screeningService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/screenings - Powinien utworzyć seans")
    void shouldCreateScreening() throws Exception {
        CreateScreeningDto dto = new CreateScreeningDto();
        dto.setMovieId(1L);
        dto.setTheaterRoomId(1L);
        dto.setStartTime(LocalDateTime.now().plusDays(1));

        when(screeningService.createScreening(any())).thenReturn(20L);

        mockMvc.perform(post("/api/screenings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("20"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/screenings/{id}/seats - Powinien zwrócić listę miejsc")
    void shouldGetSeatsForScreening() throws Exception {
        SeatDto seat = SeatDto.builder().id(1L).rowNumber(1).seatNumber(1).available(true).build();
        when(screeningService.getSeatsForScreening(1L)).thenReturn(List.of(seat));

        mockMvc.perform(get("/api/screenings/1/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].available").value(true));
    }
}