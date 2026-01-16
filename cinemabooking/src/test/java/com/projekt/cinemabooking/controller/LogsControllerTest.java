package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.controller.api.LogsController;
import com.projekt.cinemabooking.entity.SystemLog;
import com.projekt.cinemabooking.entity.enums.LogType;
import com.projekt.cinemabooking.repository.LogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogsController.class)
@AutoConfigureMockMvc(addFilters = false)
class LogsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogRepository logRepository;

    @Test
    @DisplayName("GET /api/admin/logs - Powinien zwrócić listę logów (bez filtrowania)")
    void shouldGetAllLogs() throws Exception {
        SystemLog log = SystemLog.builder().type(LogType.INFO).message("Test log").build();
        when(logRepository.getAllLogs()).thenReturn(List.of(log));

        mockMvc.perform(get("/api/admin/logs")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("INFO"))
                .andExpect(jsonPath("$[0].message").value("Test log"));
    }

    @Test
    @DisplayName("GET /api/admin/logs?type=ERROR - Powinien filtrować po typie")
    void shouldGetLogsByType() throws Exception {
        SystemLog errorLog = SystemLog.builder().type(LogType.ERROR).message("Błąd").build();
        when(logRepository.getLogsByType(LogType.ERROR)).thenReturn(List.of(errorLog));

        mockMvc.perform(get("/api/admin/logs")
                        .param("type", "ERROR")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("ERROR"));

        verify(logRepository).getLogsByType(LogType.ERROR);
    }
}