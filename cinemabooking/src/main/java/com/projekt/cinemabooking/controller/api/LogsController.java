package com.projekt.cinemabooking.controller.api;

import com.projekt.cinemabooking.entity.SystemLog;
import com.projekt.cinemabooking.entity.enums.LogType;
import com.projekt.cinemabooking.repository.LogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
@Tag(name = "Helpdesk - Logi", description = "Przegląd zdarzeń systemowych")
public class LogsController {

    private final LogRepository logRepository;

    @Operation(
            summary = "Pobierz logi",
            description = "Pobiera listę logów. Można filtrować po typie (INFO, ERROR, WARNING)."
    )
    @GetMapping
    public ResponseEntity<List<SystemLog>> getSystemLogs(
            @Parameter(description = "Opcjonalny typ logu")
            @RequestParam(required = false) LogType type
    ) {
        if (type != null) {
            return ResponseEntity.ok(logRepository.getLogsByType(type));
        }
        return ResponseEntity.ok(logRepository.getAllLogs());
    }
}