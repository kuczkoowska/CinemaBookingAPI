package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.seat.CreateSeatsDto;
import com.projekt.cinemabooking.dto.theater.TheaterRoomDto;
import com.projekt.cinemabooking.entity.TheaterRoom;
import com.projekt.cinemabooking.repository.ScreeningRepository;
import com.projekt.cinemabooking.repository.SeatRepository;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TheaterRoomServiceTest {

    @Mock
    private TheaterRoomRepository theaterRoomRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private TheaterRoomService theaterRoomService;

    @Test
    @DisplayName("Powinien wygenerować miejsca dla sali")
    void shouldGenerateSeatsForRoom() {
        CreateSeatsDto dto = new CreateSeatsDto();
        dto.setRows(5);
        dto.setSeatsPerRow(10);

        TheaterRoom room = new TheaterRoom();
        when(theaterRoomRepository.findById(1L)).thenReturn(Optional.of(room));

        theaterRoomService.generateSeatsForRoom(1L, dto);

        verify(seatRepository).saveAll(argThat(items -> ((java.util.Collection<?>) items).size() == 50));
    }

    @Test
    @DisplayName("Powinien usunąć salę, jeśli nie ma seansów")
    void shouldDeleteRoomWhenNoScreenings() {
        TheaterRoom room = new TheaterRoom();
        room.setId(1L);

        when(theaterRoomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(screeningRepository.existsByTheaterRoomId(1L)).thenReturn(false);

        theaterRoomService.deleteRoom(1L);

        verify(seatRepository).deleteAllByTheaterRoom(room);
        verify(theaterRoomRepository).delete(room);
    }

    @Test
    @DisplayName("Nie powinien usuwać sali, jeśli są seanse")
    void shouldThrowExceptionWhenDeletingRoomWithScreenings() {
        TheaterRoom room = new TheaterRoom();
        room.setId(1L);

        when(theaterRoomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(screeningRepository.existsByTheaterRoomId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> theaterRoomService.deleteRoom(1L));

        verify(theaterRoomRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Nie powinien zmieniać układu sali, jeśli są przyszłe seanse")
    void shouldThrowExceptionWhenUpdatingLayoutWithFutureScreenings() {
        when(theaterRoomRepository.findById(1L)).thenReturn(Optional.of(new TheaterRoom()));
        when(screeningRepository.existsByTheaterRoomIdAndStartTimeAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(true);

        CreateSeatsDto dto = new CreateSeatsDto();

        assertThrows(IllegalStateException.class, () -> theaterRoomService.updateSeatLayout(1L, dto));

        verify(seatRepository, never()).deleteAllByTheaterRoom(any());
    }

    @Test
    @DisplayName("Powinien utworzyć nową salę")
    void shouldCreateRoom() {
        TheaterRoomDto dto = new TheaterRoomDto();
        dto.setName("Sala A");

        TheaterRoom savedRoom = new TheaterRoom();
        savedRoom.setId(1L);
        savedRoom.setName("Sala A");

        when(theaterRoomRepository.save(any(TheaterRoom.class))).thenReturn(savedRoom);

        Long id = theaterRoomService.createRoom(dto);

        assertThat(id).isEqualTo(1L);
    }
}