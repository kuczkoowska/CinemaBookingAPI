package com.projekt.cinemabooking.controller;


import com.projekt.cinemabooking.dto.seat.CreateSeatsDto;
import com.projekt.cinemabooking.dto.theater.TheaterRoomDto;
import com.projekt.cinemabooking.service.TheaterRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
@Tag(name = "Sale", description = "Zarządzanie salami")
public class TheaterRoomController {

    private final TheaterRoomService theaterRoomService;

    @Operation(summary = "Generuj układ miejsc", description = "Automatycznie tworzy miejsca w sali (np. 10x15).")
    @PostMapping("/{roomId}/seats")
    public ResponseEntity<Void> generateSeats(@PathVariable Long roomId, @RequestBody CreateSeatsDto dto) {
        theaterRoomService.generateSeatsForRoom(roomId, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Utwórz salę", description = "Tworzy nową salę (bez miejsc).")
    @PostMapping
    public ResponseEntity<Long> createTheaterRoom(@RequestBody TheaterRoomDto dto) {
        Long roomId = theaterRoomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
    }

    @Operation(summary = "Edytuj salę", description = "Pozwala zmienić nazwę sali.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTheaterRoom(@PathVariable Long id, @RequestBody TheaterRoomDto dto) {
        theaterRoomService.editRoom(id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Usuń salę", description = "Usuwa salę, jeśli nie ma w niej zaplanowanych seansów.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheaterRoom(@PathVariable Long id) {
        theaterRoomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Zmień układ sali", description = "UWAGA: Usuwa stare fotele i tworzy nowe. Działa tylko, gdy nie ma przyszłych seansów!")
    @PutMapping("/{roomId}/seats")
    public ResponseEntity<Void> updateSeatLayout(@PathVariable Long roomId, @RequestBody CreateSeatsDto dto) {
        theaterRoomService.updateSeatLayout(roomId, dto);
        return ResponseEntity.ok().build();
    }

}
