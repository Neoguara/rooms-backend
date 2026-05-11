package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.RoomResource;
import com.neoguara.rooms.room.domain.valueobjects.RoomId;
import com.neoguara.rooms.room.domain.valueobjects.RoomResourceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomResourceJpaRepository extends JpaRepository<RoomResource, RoomResourceId> {

    List<RoomResource> findByRoomId(RoomId roomId);

    @Modifying
    @Query("DELETE FROM RoomResource r WHERE r.roomId = :roomId")
    void deleteByRoomId(@Param("roomId") RoomId roomId);
}
