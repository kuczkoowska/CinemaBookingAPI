package com.projekt.cinemabooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.controller.api.TheaterRoomController;
import com.projekt.cinemabooking.dto.input.CreateRoomDto;
import com.projekt.cinemabooking.service.TheaterRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TheaterRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
class TheaterRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TheaterRoomService theaterRoomService;

    @Test
    @DisplayName("POST /api/admin/rooms - Powinien utworzyć salę")
    void shouldCreateRoom() throws Exception {
        CreateRoomDto dto = CreateRoomDto.builder()
                .name("Sala IMAX")
                .rows(10)
                .seatsPerRow(15)
                .build();

        when(theaterRoomService.createRoom(any(CreateRoomDto.class))).thenReturn(1L);

        mockMvc.perform(post("/api/admin/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("DELETE /api/admin/rooms/{id} - Powinien usunąć salę")
    void shouldDeleteRoom() throws Exception {
        doNothing().when(theaterRoomService).deleteRoom(1L);

        mockMvc.perform(delete("/api/admin/rooms/1"))
                .andExpect(status().isNoContent()); // 204
    }
}