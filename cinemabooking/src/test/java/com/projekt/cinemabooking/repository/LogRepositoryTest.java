package com.projekt.cinemabooking.repository;

import com.projekt.cinemabooking.entity.SystemLog;
import com.projekt.cinemabooking.entity.enums.LogType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

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
        LogType type = LogType.ERROR;
        String msg = "Something went wrong";
        String email = "admin@test.pl";

        logRepository.saveLog(type, msg, email);

        List<SystemLog> logs = logRepository.getAllLogs();

        assertThat(logs).hasSize(1);

        SystemLog log = logs.get(0);
        assertThat(log.getType()).isEqualTo(LogType.ERROR);
        assertThat(log.getMessage()).isEqualTo("Something went wrong");
        assertThat(log.getUserEmail()).isEqualTo("admin@test.pl");
        assertThat(log.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFilterLogsByType() {
        logRepository.saveLog(LogType.INFO, "Login success", "user@test.pl");
        logRepository.saveLog(LogType.ERROR, "DB connection fail", "admin@test.pl");
        logRepository.saveLog(LogType.INFO, "Logout", "user@test.pl");

        List<SystemLog> infoLogs = logRepository.getLogsByType(LogType.INFO);

        assertThat(infoLogs).hasSize(2);
        assertThat(infoLogs)
                .extracting(SystemLog::getType)
                .containsOnly(LogType.INFO);
    }
}