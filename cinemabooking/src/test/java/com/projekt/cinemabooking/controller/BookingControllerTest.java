//package com.projekt.cinemabooking.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.projekt.cinemabooking.controller.api.BookingController;
//import com.projekt.cinemabooking.dto.input.LockSeatsDto;
//import com.projekt.cinemabooking.dto.input.UpdateTicketTypeDto;
//import com.projekt.cinemabooking.dto.output.BookingDto;
//import com.projekt.cinemabooking.entity.Role;
//import com.projekt.cinemabooking.entity.User;
//import com.projekt.cinemabooking.entity.enums.TicketType;
//import com.projekt.cinemabooking.security.CustomUserDetails;
//import com.projekt.cinemabooking.service.BookingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.Set;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BookingController.class)
//class BookingControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockitoBean
//    private BookingService bookingService;
//
//    private CustomUserDetails customUserDetails;
//
//    @BeforeEach
//    void setUp() {
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("test@test.pl");
//        user.setPassword("pass");
//        user.setRoles(Set.of(new Role(1L, "ROLE_USER")));
//        user.setActive(true);
//
//        customUserDetails = new CustomUserDetails(user);
//    }
//
//    @Test
//    @DisplayName("POST /lock - Powinien zablokować miejsca")
//    void shouldLockSeats() throws Exception {
//        LockSeatsDto dto = new LockSeatsDto();
//        dto.setScreeningId(10L);
//        dto.setSeatIds(List.of(100L, 101L));
//
//        BookingDto responseDto = new BookingDto();
//        responseDto.setId(50L);
//
//        when(bookingService.lockSeats(any(LockSeatsDto.class), eq(1L))).thenReturn(responseDto);
//
//        mockMvc.perform(post("/api/bookings/lock")
//                        .with(csrf())
//                        .with(user(customUserDetails))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @DisplayName("PUT /tickets - Powinien zaktualizować typy biletów")
//    void shouldUpdateTicketTypes() throws Exception {
//        UpdateTicketTypeDto updateDto = new UpdateTicketTypeDto(10L, TicketType.ULGOWY);
//        List<UpdateTicketTypeDto> list = List.of(updateDto);
//
//        BookingDto responseDto = new BookingDto();
//        responseDto.setId(1L);
//
//        when(bookingService.updateTicketTypes(eq(1L), any(), eq(1L))).thenReturn(responseDto);
//
//        mockMvc.perform(put("/api/bookings/1/tickets")
//                        .with(csrf())
//                        .with(user(customUserDetails))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(list)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("POST /pay - Powinien potwierdzić rezerwację")
//    void shouldConfirmBooking() throws Exception {
//        doNothing().when(bookingService).confirmBooking(eq(1L), eq(1L));
//
//        mockMvc.perform(post("/api/bookings/1/pay")
//                        .with(csrf())
//                        .with(user(customUserDetails)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("PATCH /cancel - Powinien anulować rezerwację")
//    void shouldCancelBooking() throws Exception {
//        doNothing().when(bookingService).cancelBooking(eq(1L), eq(1L));
//
//        mockMvc.perform(patch("/api/bookings/1/cancel")
//                        .with(csrf())
//                        .with(user(customUserDetails)))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    @DisplayName("GET /api/bookings/{id} - Powinien pobrać szczegóły")
//    void shouldGetBookingDetails() throws Exception {
//        BookingDto dto = new BookingDto();
//        dto.setId(1L);
//
//        when(bookingService.getBookingById(eq(1L), eq(1L))).thenReturn(dto);
//
//        mockMvc.perform(get("/api/bookings/1")
//                        .with(user(customUserDetails)))
//                .andExpect(status().isOk());
//    }
//}