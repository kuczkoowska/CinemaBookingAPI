-- ====================================
-- CINEMA BOOKING - SEED DATA
-- ====================================

-- Tworzenie tabeli logów systemowych (jeśli nie istnieje)
DROP TABLE IF EXISTS system_logs;
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    message TEXT,
    user_email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ====================================
-- 1. ROLE UŻYTKOWNIKÓW
-- ====================================
INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

-- ====================================
-- 3. PRZYPISANIE RÓL (Tutaj też zmieniamy users na app_users w SELECT)
-- ====================================
-- Admin ma obie role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM app_users u, roles r
WHERE u.email = 'admin@cinema.pl' AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM app_users u, roles r
WHERE u.email = 'admin@cinema.pl' AND r.name = 'ROLE_USER'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- Pozostali mają tylko ROLE_USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM app_users u, roles r
WHERE u.email IN ('user@cinema.pl', 'blocked@cinema.pl', 'maria@test.pl') AND r.name = 'ROLE_USER'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- ====================================
-- 4. FILMY
-- ====================================
INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Incepcja', 'SCI_FI',
       'Dom Cobb (Leonardo DiCaprio) jest najlepszym złodziejem świata. Jego specjalność to wykradanie bezcennych tajemnic z nieświadomości, podczas gdy umysł ofiary jest najbardziej podatny – w trakcie snu.',
       'Christopher Nolan', 148, 12,
       'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_FMjpg_UX1000_.jpg',
       'https://www.youtube.com/watch?v=YoHD9XEInc0'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Incepcja');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Król Lew', 'ANIMACJA',
       'Simba, młody lwiątko, zostaje oszukany przez swojego wuja Skara i musi uciekać ze swojej ojczyzny. Po latach powraca, aby odzyskać swoje królestwo.',
       'Roger Allers, Rob Minkoff', 88, 0,
       'https://m.media-amazon.com/images/M/MV5BYTYxNGMyZTYtMjE3MS00MzNjLWFjNmYtMDk3N2FmM2JiM2M1XkEyXkFqcGdeQXVyNjY5NDU4NzI@._V1_.jpg',
       'https://www.youtube.com/watch?v=_u64jr-u_oA'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Król Lew');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Ojciec chrzestny', 'DRAMAT',
       'Opowieść o nowojorskiej rodzinie mafijnej. Starzejący się Don Corleone pragnie przekazać władzę synowi.',
       'Francis Ford Coppola', 175, 16,
       'https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg',
       'https://www.youtube.com/watch?v=UaVTIH8mujA'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Ojciec chrzestny');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Pulp Fiction', 'THRILLER',
       'Kilka historii z Los Angeles, które przeplatają się ze sobą. Główni bohaterowie to płatni mordercy, żona gangstera i dwójka amatorów.',
       'Quentin Tarantino', 154, 18,
       'https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg',
       'https://www.youtube.com/watch?v=s7EdQ4FqbhY'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Pulp Fiction');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Gladiator', 'AKCJA',
       'Maximus (Russell Crowe), generał armii rzymskiej, zostaje zdradzony przez syna cesarza. Jako niewolnik staje się gladiatorem, aby pomścić śmierć swojej rodziny.',
       'Ridley Scott', 155, 16,
       'https://m.media-amazon.com/images/M/MV5BMDliMmNhNDEtODUyOS00MjNlLTgxODEtN2U3NzIxMGVkZTA1L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg',
       'https://www.youtube.com/watch?v=owK1qxDselE'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Gladiator');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Parasite', 'THRILLER',
       'Rodzina Kimów żyje w biedzie. Pewnego dnia syn dostaje pracę jako korepetytor w domu bogatej rodziny Parków. To początek serii zaskakujących wydarzeń.',
       'Bong Joon-ho', 132, 16,
       'https://m.media-amazon.com/images/M/MV5BYWZjMjk3ZTItODQ2ZC00NTY5LWE0ZDYtZTI3MjcwN2Q5NTVkXkEyXkFqcGdeQXVyODk4OTc3MTY@._V1_.jpg',
       'https://www.youtube.com/watch?v=5xH0HfJHsaY'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Parasite');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Władca Pierścieni: Drużyna Pierścienia', 'FANTASY',
       'Młody hobbit Frodo otrzymuje zadanie zniszczenia potężnego pierścienia, który może przywrócić do życia mrocznego władcę Saurona.',
       'Peter Jackson', 178, 12,
       'https://m.media-amazon.com/images/M/MV5BN2EyZjM3NzUtNWUzMi00MTgxLWI0NTctMzY4M2VlOTdjZWRiXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_.jpg',
       'https://www.youtube.com/watch?v=V75dMMIW2B4'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Vladca Pierścieni: Drużyna Pierścienia');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Joker', 'DRAMAT',
       'Arthur Fleck (Joaquin Phoenix), nieudany komik cierpiący na zaburzenia psychiczne, stopniowo przekształca się w charyzmatycznego przestępcę znanego jako Joker.',
       'Todd Phillips', 122, 18,
       'https://m.media-amazon.com/images/M/MV5BNGVjNWI4ZGUtNzE0MS00YTJmLWE0ZDctN2ZiYTk2YmI3NTYyXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_.jpg',
       'https://www.youtube.com/watch?v=zAGVQLHvwOY'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Joker');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Avengers: Endgame', 'AKCJA',
       'Po dewastujących wydarzeniach z Infinity War, Avengersi ponownie się zbierają, aby odwrócić działania Thanosa i przywrócić równowagę we wszechświecie.',
       'Anthony Russo, Joe Russo', 181, 12,
       'https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_.jpg',
       'https://www.youtube.com/watch?v=TcMBFSGVi1c'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Avengers: Endgame');

INSERT INTO movies (title, genre, description, director, duration_minutes, age_rating, poster_url, trailer_url)
SELECT 'Coco', 'ANIMACJA',
       'Miguel, młody chłopiec marzy o karierze muzyka, mimo że w jego rodzinie muzyka jest zakazana. Pewnego dnia trafia do Krainy Umarłych.',
       'Lee Unkrich', 105, 0,
       'https://m.media-amazon.com/images/M/MV5BYjQ5NjM0Y2YtNjZkNC00ZDhkLWJjMWItN2QyNzFkMDE3ZjAxXkEyXkFqcGdeQXVyODIxMzk5NjA@._V1_.jpg',
       'https://www.youtube.com/watch?v=Rvr68u6k5sI'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE title = 'Coco');

-- ====================================
-- 5. SALE KINOWE
-- ====================================
INSERT INTO theater_rooms (name, total_rows, seats_per_row)
SELECT 'Sala 1 - Główna', 10, 10
WHERE NOT EXISTS (SELECT 1 FROM theater_rooms WHERE name = 'Sala 1 - Główna');

INSERT INTO theater_rooms (name, total_rows, seats_per_row)
SELECT 'Sala 2 - VIP', 8, 12
WHERE NOT EXISTS (SELECT 1 FROM theater_rooms WHERE name = 'Sala 2 - VIP');

INSERT INTO theater_rooms (name, total_rows, seats_per_row)
SELECT 'Sala 3 - Mała', 6, 8
WHERE NOT EXISTS (SELECT 1 FROM theater_rooms WHERE name = 'Sala 3 - Mała');

-- ====================================
-- 6. SEANSE (przykładowe na najbliższe dni)
-- ====================================
-- Incepcja - dzisiaj wieczór
INSERT INTO screenings (movie_id, theater_room_id, start_time, end_time)
SELECT m.id, tr.id,
       DATEADD('HOUR', 19, CURRENT_DATE),
       DATEADD('MINUTE', m.duration_minutes, DATEADD('HOUR', 19, CURRENT_DATE))
FROM movies m, theater_rooms tr
WHERE m.title = 'Incepcja' AND tr.name = 'Sala 1 - Główna'
  AND NOT EXISTS (
    SELECT 1 FROM screenings s
    WHERE s.movie_id = m.id AND s.start_time = DATEADD('HOUR', 19, CURRENT_DATE)
);

-- Król Lew - jutro popołudnie
INSERT INTO screenings (movie_id, theater_room_id, start_time, end_time)
SELECT m.id, tr.id,
       DATEADD('DAY', 1, DATEADD('HOUR', 15, CURRENT_DATE)),
       DATEADD('MINUTE', m.duration_minutes, DATEADD('DAY', 1, DATEADD('HOUR', 15, CURRENT_DATE)))
FROM movies m, theater_rooms tr
WHERE m.title = 'Król Lew' AND tr.name = 'Sala 3 - Mała'
  AND NOT EXISTS (
    SELECT 1 FROM screenings s
    WHERE s.movie_id = m.id AND s.start_time = DATEADD('DAY', 1, DATEADD('HOUR', 15, CURRENT_DATE))
);

-- Gladiator - pojutrze wieczór
INSERT INTO screenings (movie_id, theater_room_id, start_time, end_time)
SELECT m.id, tr.id,
       DATEADD('DAY', 2, DATEADD('HOUR', 20, CURRENT_DATE)),
       DATEADD('MINUTE', m.duration_minutes, DATEADD('DAY', 2, DATEADD('HOUR', 20, CURRENT_DATE)))
FROM movies m, theater_rooms tr
WHERE m.title = 'Gladiator' AND tr.name = 'Sala 2 - VIP'
  AND NOT EXISTS (
    SELECT 1 FROM screenings s
    WHERE s.movie_id = m.id AND s.start_time = DATEADD('DAY', 2, DATEADD('HOUR', 20, CURRENT_DATE))
);

-- Avengers: Endgame - za 3 dni
INSERT INTO screenings (movie_id, theater_room_id, start_time, end_time)
SELECT m.id, tr.id,
       DATEADD('DAY', 3, DATEADD('HOUR', 18, CURRENT_DATE)),
       DATEADD('MINUTE', m.duration_minutes, DATEADD('DAY', 3, DATEADD('HOUR', 18, CURRENT_DATE)))
FROM movies m, theater_rooms tr
WHERE m.title = 'Avengers: Endgame' AND tr.name = 'Sala 1 - Główna'
  AND NOT EXISTS (
    SELECT 1 FROM screenings s
    WHERE s.movie_id = m.id AND s.start_time = DATEADD('DAY', 3, DATEADD('HOUR', 18, CURRENT_DATE))
);

-- Joker - za 4 dni
INSERT INTO screenings (movie_id, theater_room_id, start_time, end_time)
SELECT m.id, tr.id,
       DATEADD('DAY', 4, DATEADD('HOUR', 21, CURRENT_DATE)),
       DATEADD('MINUTE', m.duration_minutes, DATEADD('DAY', 4, DATEADD('HOUR', 21, CURRENT_DATE)))
FROM movies m, theater_rooms tr
WHERE m.title = 'Joker' AND tr.name = 'Sala 2 - VIP'
  AND NOT EXISTS (
    SELECT 1 FROM screenings s
    WHERE s.movie_id = m.id AND s.start_time = DATEADD('DAY', 4, DATEADD('HOUR', 21, CURRENT_DATE))
);

-- Coco - za 5 dni (popołudnie dla dzieci)
INSERT INTO screenings (movie_id, theater_room_id, start_time, end_time)
SELECT m.id, tr.id,
       DATEADD('DAY', 5, DATEADD('HOUR', 14, CURRENT_DATE)),
       DATEADD('MINUTE', m.duration_minutes, DATEADD('DAY', 5, DATEADD('HOUR', 14, CURRENT_DATE)))
FROM movies m, theater_rooms tr
WHERE m.title = 'Coco' AND tr.name = 'Sala 3 - Mała'
  AND NOT EXISTS (
    SELECT 1 FROM screenings s
    WHERE s.movie_id = m.id AND s.start_time = DATEADD('DAY', 5, DATEADD('HOUR', 14, CURRENT_DATE))
);

-- ====================================
-- 7. CENY BILETÓW
-- ====================================
INSERT INTO ticket_prices (ticket_type, price)
SELECT 'NORMALNY', 25.00
WHERE NOT EXISTS (SELECT 1 FROM ticket_prices WHERE ticket_type = 'NORMALNY');

INSERT INTO ticket_prices (ticket_type, price)
SELECT 'ULGOWY', 20.00
WHERE NOT EXISTS (SELECT 1 FROM ticket_prices WHERE ticket_type = 'ULGOWY');

-- ====================================
-- 8. LOGI SYSTEMOWE (przykładowe)
-- ====================================
INSERT INTO system_logs (type, message, user_email)
VALUES ('INFO', 'System uruchomiony pomyślnie', NULL);

INSERT INTO system_logs (type, message, user_email)
VALUES ('INFO', 'Zainicjalizowano dane testowe', NULL);
