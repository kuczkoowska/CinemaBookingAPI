package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.dto.admin.SalesStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SalesStatisticsRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<SalesStatsDto> getSalesByDate() {
        String sql = """
                    SELECT 
                        CAST(b.booking_time AS DATE) as sale_date, 
                        COUNT(t.id) as tickets_count, 
                        SUM(t.price) as revenue
                    FROM bookings b
                    JOIN tickets t ON b.id = t.booking_id
                    WHERE b.status = 'OPLACONA'
                    GROUP BY CAST(b.booking_time AS DATE)
                    ORDER BY sale_date DESC
                """;

        return jdbcTemplate.query(sql, new SalesStatsRowMapper());
    }

    public void logAction(String message) {
        String sql = "INSERT INTO system_logs (message, created_at) VALUES (?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, message);
    }

    public List<String> getAllSystemLogs() {
        String sql = "SELECT message, created_at FROM system_logs ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getString("message") + " (" + rs.getTimestamp("created_at") + ")"
        );
    }

    private static class SalesStatsRowMapper implements RowMapper<SalesStatsDto> {
        @Override
        public SalesStatsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SalesStatsDto(
                    rs.getObject("sale_date", LocalDate.class),
                    rs.getInt("tickets_count"),
                    rs.getBigDecimal("revenue")
            );
        }
    }
}
