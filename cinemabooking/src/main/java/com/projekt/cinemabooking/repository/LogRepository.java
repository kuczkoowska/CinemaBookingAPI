package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.SystemLog;
import com.projekt.cinemabooking.entity.enums.LogType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveLog(LogType type, String message, String email) {
        String sql = "INSERT INTO system_logs (type, message, user_email, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, type.name(), message, email, LocalDateTime.now());
    }

    public List<SystemLog> getLogsByType(LogType type) {
        String sql = "SELECT * FROM system_logs WHERE type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, this::mapRow, type.name());
    }

    private SystemLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SystemLog.builder()
                .id(rs.getLong("id"))
                .type(LogType.valueOf(rs.getString("type")))
                .message(rs.getString("message"))
                .userEmail(rs.getString("user_email"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }

    public List<SystemLog> getAllLogs() {
        String sql = "SELECT * FROM system_logs";
        return jdbcTemplate.query(sql, this::mapRow);
    }
}
