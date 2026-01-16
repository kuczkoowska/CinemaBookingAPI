package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.controller.api.StatsController;
import com.projekt.cinemabooking.dto.output.SalesStatsDto;
import com.projekt.cinemabooking.repository.SalesStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StatsController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalesStatisticsRepository statsRepository;

    @Test
    @DisplayName("GET /api/admin/stats - Powinien zwrócić statystyki")
    void shouldGetStats() throws Exception {
        SalesStatsDto dto = new SalesStatsDto(LocalDate.now(), 5, BigDecimal.TEN);

        when(statsRepository.getSalesByDate(anyString(), anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketsSold").value(5))
                .andExpect(jsonPath("$[0].totalRevenue").value(10));
    }
}