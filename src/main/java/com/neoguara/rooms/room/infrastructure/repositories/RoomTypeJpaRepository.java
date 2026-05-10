package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeJpaRepository extends JpaRepository<RoomType, RoomTypeId> {
}
