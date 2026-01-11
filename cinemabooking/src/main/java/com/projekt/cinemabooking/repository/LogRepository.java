package com.projekt.cinemabooking.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveLog(String type, String message, String email) {
        String sql = "INSERT INTO system_logs (type, message, user_email, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, type, message, email, LocalDateTime.now());
    }

    public List<Map<String, Object>> getLogsByType(String type) {
        String sql = "SELECT * FROM system_logs WHERE type = ? ORDER BY created_at DESC";
        return jdbcTemplate.queryForList(sql, type);
    }

    public List<Map<String, Object>> getAllLogs() {
        String sql = "SELECT * FROM system_logs ORDER BY created_at DESC";
        return jdbcTemplate.queryForList(sql);
    }
}
