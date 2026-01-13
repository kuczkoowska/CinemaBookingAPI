package com.projekt.cinemabooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.dto.booking.BookingDto;
import com.projekt.cinemabooking.dto.seat.LockSeatsDto;
import com.projekt.cinemabooking.dto.ticket.UpdateTicketTypeDto;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookingService bookingService;

    @Test
    @WithMockUser
    @DisplayName("POST /lock - Powinien zablokować miejsca")
    void shouldLockSeats() throws Exception {
        LockSeatsDto dto = new LockSeatsDto();
        dto.setScreeningId(1L);
        dto.setSeatIds(List.of(10L, 11L));
        dto.setUserId(5L);

        when(bookingService.lockSeats(any(LockSeatsDto.class))).thenReturn(100L);

        mockMvc.perform(post("/api/bookings/lock")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("100"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /tickets - Powinien zaktualizować typy biletów")
    void shouldUpdateTicketTypes() throws Exception {
        UpdateTicketTypeDto updateDto = new UpdateTicketTypeDto(1L, TicketType.ULGOWY);
        List<UpdateTicketTypeDto> list = List.of(updateDto);

        BookingDto responseDto = new BookingDto();
        responseDto.setId(1L);
        responseDto.setTotalAmount(15.0);

        when(bookingService.updateTicketTypes(eq(1L), anyList())).thenReturn(responseDto);

        mockMvc.perform(put("/api/bookings/1/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(15.0));
    }

    @Test
    @WithMockUser(username = "jan@test.pl")
    @DisplayName("PATCH /cancel - Powinien anulować rezerwację")
    void shouldCancelBooking() throws Exception {
        doNothing().when(bookingService).cancelBooking(eq(1L), eq("jan@test.pl"));

        mockMvc.perform(patch("/api/bookings/1/cancel")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /pay - Powinien potwierdzić rezerwację")
    void shouldConfirmBooking() throws Exception {
        mockMvc.perform(post("/api/bookings/1/pay")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}