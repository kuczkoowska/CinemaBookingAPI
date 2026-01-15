package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.repository.LogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
@Tag(name = "Helpdesk - logi", description = "Wszystkie logi jakie się pojawiają w systemie")
public class LogsController {

    private final LogRepository logRepository;


    @Operation(summary = "Logi systemowe", description = "Wyświetla historię zdarzeń")
    @GetMapping()
    public ResponseEntity<List<Map<String, Object>>> getSystemLogs() {
        return ResponseEntity.ok(logRepository.getAllLogs());
    }
}
