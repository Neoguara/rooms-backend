package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.RoomType;
import com.neoguara.rooms.room.domain.enums.RoomTypeStatus;
import com.neoguara.rooms.room.domain.valueobjects.RoomTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomTypeJpaRepository extends JpaRepository<RoomType, RoomTypeId> {
    List<RoomType> findAllByStatusNot(RoomTypeStatus status);
    Optional<RoomType> findByIdAndStatusNot(RoomTypeId id, RoomTypeStatus status);
}
