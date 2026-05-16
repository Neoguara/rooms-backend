package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingJpaRepository extends JpaRepository<Building, BuildingId> {
    List<Building> findAllByStatusNot(BuildingStatus status);
    Optional<Building> findByIdAndStatusNot(BuildingId id, BuildingStatus status);
}
