package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.Room;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<Room, RoomId> {
}
