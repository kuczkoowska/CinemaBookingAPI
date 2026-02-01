# Cinema Booking - Backend API

System zarządzania rezerwacjami w kinie - REST API oparte na Spring Boot.

## Wymagania środowiskowe

### Niezbędne oprogramowanie

- **Java Development Kit (JDK)**: 21 lub nowsza wersja
    - Sprawdź wersję: `java -version`
    - Pobierz z: [https://adoptium.net/](https://adoptium.net/)

- **Maven**: 3.9+ (opcjonalnie - projekt zawiera Maven Wrapper)
    - Sprawdź wersję: `mvn -version`
    - Pobierz z: [https://maven.apache.org/](https://maven.apache.org/)

### Technologie i zależności

- **Spring Boot**: 3.5.9
- **Spring Security**: Session-based authentication
- **Spring Data JPA**: Persystencja danych
- **H2 Database**: Wbudowana baza danych (plik: `./data/cinemabooking.mv.db`)
- **MapStruct**: 1.5.5.Final - automatyczne mapowanie DTO
- **Lombok**: Redukcja boilerplate code
- **Validation API**: Walidacja danych wejściowych
- **JaCoCo**: Pokrycie testów

## Instrukcja uruchomienia

### Metoda 1: Używając Maven Wrapper (zalecana)

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

### Metoda 2: Używając zainstalowanego Maven

```bash
mvn spring-boot:run
```

## Dostęp do aplikacji

Po uruchomieniu aplikacja będzie dostępna pod adresem:

- **API**: `http://localhost:8080`
- **Konsola H2**: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:file:./data/cinemabooking`
    - Username: `sa`
    - Password: *(puste)*

## Domyślne konta użytkowników

Aplikacja automatycznie tworzy przykładowych użytkowników przy pierwszym uruchomieniu:

### Administrator

- **Email**: `admin@cinema.pl`
- **Hasło**: `admin123`
- **Role**: ADMIN, USER

### Użytkownik

- **Email**: `user@cinema.pl`
- **Hasło**: `user123`
- **Role**: USER

### Użytkownik zablokowany

- **Email**: `blocked@cinema.pl`
- **Hasło**: `blocked123`
- **Role**: USER
- **Status**: Zablokowany

## Struktura projektu

```
src/
├── main/
│   ├── java/com/projekt/cinemabooking/
│   │   ├── config/          # Konfiguracja Spring Security, CORS
│   │   ├── controller/      # REST kontrolery
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Encje JPA
│   │   ├── enums/           # Enumy (MovieGenre, TicketType, etc.)
│   │   ├── exception/       # Obsługa wyjątków
│   │   ├── mapper/          # MapStruct mappery
│   │   ├── repository/      # Repozytoria JPA
│   │   ├── security/        # UserDetails, AuthService
│   │   └── service/         # Logika biznesowa
│   └── resources/
│       ├── application.yml  # Konfiguracja aplikacji
│       └── data.sql         # Skrypt inicjalizacji danych
└── test/                    # Testy jednostkowe i integracyjne
```

## Uruchamianie testów

```bash
# Wszystkie testy
./mvnw test

# Testy z raportem pokrycia (JaCoCo)
./mvnw clean test jacoco:report

# Raport dostępny w: target/site/jacoco/index.html
```

## Dodatkowe informacje

- **CORS**: Skonfigurowany dla `http://localhost:4200` (Angular)
- **Session Management**: Sesje HTTP z ciasteczkami (SameSite=Lax)
- **Walidacja**: Bean Validation na wszystkich DTO
- **Logowanie**: SLF4J z Logback (poziom: DEBUG dla com.projekt.cinemabooking)

