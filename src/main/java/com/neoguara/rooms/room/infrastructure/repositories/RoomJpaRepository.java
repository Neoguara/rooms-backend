package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.enums.RoomStatus;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomJpaRepository extends JpaRepository<Room, RoomId> {
    List<Room> findAllByStatusNot(RoomStatus status);
    Optional<Room> findByIdAndStatusNot(RoomId id, RoomStatus status);
}
