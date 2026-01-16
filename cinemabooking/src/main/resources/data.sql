DROP TABLE IF EXISTS system_logs;

CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    message TEXT,
    user_email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating)
SELECT 'Incepcja', 'SCI_FI', 'Czasy, gdy technologia pozwala na wchodzenie w sny...', 'Christopher Nolan', 148, 12
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Incepcja');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating)
SELECT 'Król Lew', 'ANIMACJA', 'Simba, młody lwiątko, musi odzyskać królestwo.', 'Roger Allers', 88, 0
    WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Król Lew');

INSERT INTO theater_rooms (name, total_rows, seats_per_row)
SELECT 'Sala 1 - Główna', 10, 10
    WHERE NOT EXISTS (SELECT 1 FROM theater_rooms WHERE name = 'Sala 1 - Główna');