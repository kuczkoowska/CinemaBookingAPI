package com.projekt.cinemabooking.controller.api;


import com.projekt.cinemabooking.dto.input.CreateRoomDto;
import com.projekt.cinemabooking.service.TheaterRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
@Tag(name = "Sale", description = "Zarządzanie salami kinowymi")
public class TheaterRoomController {

    private final TheaterRoomService theaterRoomService;

    @Operation(summary = "Utwórz salę", description = "Tworzy nową salę wraz z układem miejsc.")
    @PostMapping
    public ResponseEntity<Long> createTheaterRoom(@Valid @RequestBody CreateRoomDto dto) {
        Long roomId = theaterRoomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
    }

    @Operation(summary = "Usuń salę", description = "Usuwa salę, jeśli nie ma w niej zaplanowanych seansów.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheaterRoom(@PathVariable Long id) {
        theaterRoomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
