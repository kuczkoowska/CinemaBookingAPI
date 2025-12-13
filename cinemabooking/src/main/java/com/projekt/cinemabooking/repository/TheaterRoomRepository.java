package com.projekt.cinemabooking.repository;


import com.projekt.cinemabooking.entity.TheaterRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRoomRepository extends JpaRepository<TheaterRoom, Long> {
}