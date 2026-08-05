package com.neoguara.rooms.room.application.ports;

import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;

import java.util.List;
import java.util.Optional;

public interface BuildingRepositoryPort {
    Building save(Building building);
    Optional<Building> findById(BuildingId id);
    List<Building> findAll();
}
