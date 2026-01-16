package com.projekt.cinemabooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.controller.api.TicketPriceController;
import com.projekt.cinemabooking.dto.input.UpdateTicketPriceDto;
import com.projekt.cinemabooking.entity.TicketPrice;
import com.projekt.cinemabooking.entity.enums.TicketType;
import com.projekt.cinemabooking.service.TicketPriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketPriceController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketPriceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private TicketPriceService ticketPriceService;

    @Test
    @DisplayName("GET /api/ticket-prices - Powinien zwrócić listę cen")
    void shouldReturnAllPrices() throws Exception {
        TicketPrice price = new TicketPrice(1L, TicketType.NORMALNY, BigDecimal.valueOf(25));
        when(ticketPriceService.getAllPrices()).thenReturn(List.of(price));

        mockMvc.perform(get("/api/ticket-prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketType").value("NORMALNY"))
                .andExpect(jsonPath("$[0].price").value(25));
    }

    @Test
    @DisplayName("PUT /api/ticket-prices/{type} - Powinien zaktualizować cenę")
    void shouldUpdatePrice() throws Exception {
        UpdateTicketPriceDto dto = new UpdateTicketPriceDto(BigDecimal.valueOf(30));

        TicketPrice updated = new TicketPrice(1L, TicketType.NORMALNY, BigDecimal.valueOf(30));
        when(ticketPriceService.updatePrice(eq(TicketType.NORMALNY), any())).thenReturn(updated);

        mockMvc.perform(put("/api/ticket-prices/NORMALNY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(30));
    }
}

