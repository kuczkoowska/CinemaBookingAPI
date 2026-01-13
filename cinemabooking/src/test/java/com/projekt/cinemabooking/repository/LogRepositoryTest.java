package com.projekt.cinemabooking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(LogRepository.class)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.liquibase.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=never"
})
class LogRepositoryTest {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("" +
                "CREATE TABLE IF NOT EXISTS system_logs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "type VARCHAR(50), " +
                "message VARCHAR(255), " +
                "user_email VARCHAR(100), " +
                "created_at TIMESTAMP)");

        jdbcTemplate.execute("TRUNCATE TABLE system_logs");
    }

    @Test
    void shouldSaveAndRetrieveLog() {
        String type = "ERROR";
        String msg = "Something went wrong";
        String email = "admin@test.pl";

        logRepository.saveLog(type, msg, email);

        List<Map<String, Object>> logs = logRepository.getAllLogs();
        assertThat(logs).hasSize(1);
        assertThat(logs.getFirst().get("TYPE")).isEqualTo("ERROR");
        assertThat(logs.getFirst().get("MESSAGE")).isEqualTo("Something went wrong");
    }

    @Test
    void shouldFilterLogsByType() {
        logRepository.saveLog("INFO", "Login success", "user@test.pl");
        logRepository.saveLog("ERROR", "DB connection fail", "admin@test.pl");
        logRepository.saveLog("INFO", "Logout", "user@test.pl");

        List<Map<String, Object>> infoLogs = logRepository.getLogsByType("INFO");

        assertThat(infoLogs).hasSize(2);
        assertThat(infoLogs).extracting(map -> map.get("TYPE")).containsOnly("INFO");
    }
}