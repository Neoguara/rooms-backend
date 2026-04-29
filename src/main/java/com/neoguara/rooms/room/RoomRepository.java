package com.neoguara.rooms.room;

import com.neoguara.rooms.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<User, UUID> {
}
