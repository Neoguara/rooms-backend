package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingJpaRepository extends JpaRepository<Building, BuildingId> {
}
