package com.projekt.cinemabooking.controller;

import com.projekt.cinemabooking.dto.user.UpdateUserDto;
import com.projekt.cinemabooking.dto.user.UserAdminDto;
import com.projekt.cinemabooking.dto.user.UserDto;
import com.projekt.cinemabooking.security.CustomUserDetails;
import com.projekt.cinemabooking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Użytkownicy", description = "Zarządzanie kontami użytkowników i administratorów")
public class UserController {

    private final UserService userService;


    @Operation(summary = "Pobierz wszystkich użytkowników", description = "Zwraca pełną listę użytkowników wraz z rolami i statusem blokady. Tylko dla Admina.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserAdminDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Pobierz dane użytkownika po ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Znaleziono użytkownika"),
            @ApiResponse(responseCode = "404", description = "Nie ma takiego ID")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAdminDto> getUserDataById(@Parameter(description = "ID użytkownika") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Edytuj użytkownika (Jako Admin)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAdminDto> updateUserById(
            @Parameter(description = "ID edytowanego użytkownika") @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto dto
    ) {
        return ResponseEntity.ok(userService.updateUserAsAdmin(id, dto));
    }

    @Operation(summary = "Zablokuj/Odblokuj konto")
    @PatchMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleBlockUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        userService.toggleBlockUser(id, currentUser.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Awansuj na Administratora")
    @PatchMapping("/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable Long id) {
        userService.promoteToAdmin(id);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Pobierz moje dane", description = "Zwraca profil zalogowanego użytkownika.")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMyData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserDtoById(userDetails.getUser().getId()));
    }

    @Operation(summary = "Edytuj moje dane")
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateMyData(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateUserDto dto
    ) {
        return ResponseEntity.ok(userService.updateUser(userDetails.getUser().getId(), dto));
    }
}