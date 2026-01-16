package com.projekt.cinemabooking.service;

import com.projekt.cinemabooking.dto.input.CreateRoomDto;
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
    @DisplayName("Powinien utworzyć salę I wygenerować miejsca")
    void shouldCreateRoomAndSeats() {
        CreateRoomDto dto = CreateRoomDto.builder()
                .name("Sala A")
                .rows(5)
                .seatsPerRow(10)
                .build();

        TheaterRoom savedRoom = new TheaterRoom();
        savedRoom.setId(1L);
        savedRoom.setName("Sala A");

        when(theaterRoomRepository.save(any(TheaterRoom.class))).thenReturn(savedRoom);

        Long id = theaterRoomService.createRoom(dto);

        assertThat(id).isEqualTo(1L);

        verify(theaterRoomRepository).save(any(TheaterRoom.class));

        verify(seatRepository).saveAll(argThat(items ->
                ((java.util.Collection<?>) items).size() == 50
        ));
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
}