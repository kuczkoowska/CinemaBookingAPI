package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.input.RegisterDto;
import com.projekt.cinemabooking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autentykacja", description = "Endpointy do rejestracji i logowania")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Rejestracja nowego użytkownika")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Użytkownik zarejestrowany pomyślnie"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji lub email zajęty")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        authService.registerUser(registerDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Rejestracja udana! Możesz się zalogować."));
    }

    @Operation(summary = "Potwierdzenie logowania (Techniczny)", description = "Zwraca komunikat sukcesu. Używany przy przekierowaniach po loginie formularzowym.")
    @ApiResponse(responseCode = "200", description = "Zalogowano", content = @Content(examples = @ExampleObject(value = "Zalogowano pomyślnie!")))
    @GetMapping("/success")
    public ResponseEntity<String> loginSuccess() {
        return ResponseEntity.ok("Zalogowano pomyślnie!");
    }

    @Operation(summary = "Błąd logowania (Techniczny)", description = "Zwraca komunikat o błędzie autentykacji.")
    @ApiResponse(responseCode = "401", description = "Brak autoryzacji", content = @Content(examples = @ExampleObject(value = "Błędny login lub hasło")))
    @GetMapping("/failure")
    public ResponseEntity<String> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Błędny login lub hasło");
    }
}
