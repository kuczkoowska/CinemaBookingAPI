package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.admin.SalesStatsDto;
import com.projekt.cinemabooking.repository.LogRepository;
import com.projekt.cinemabooking.repository.SalesStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({LogsController.class, StatsController.class})
class LogsAndStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LogRepository logRepository;
    @MockitoBean
    private SalesStatisticsRepository salesStatisticsRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/logs - Powinien zwrócić listę map")
    void shouldGetLogs() throws Exception {
        when(logRepository.getAllLogs()).thenReturn(List.of(Map.of("type", "INFO")));

        mockMvc.perform(get("/api/admin/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("INFO"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/stats - Powinien zwrócić statystyki")
    void shouldGetStats() throws Exception {
        SalesStatsDto dto = new SalesStatsDto(LocalDate.now(), 5, BigDecimal.TEN);
        when(salesStatisticsRepository.getSalesByDate(anyString(), anyString())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketsSold").value(5));
    }
}