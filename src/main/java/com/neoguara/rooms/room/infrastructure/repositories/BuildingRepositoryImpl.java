package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.application.ports.BuildingRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Building;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BuildingRepositoryImpl implements BuildingRepositoryPort {

    private final BuildingJpaRepository jpaRepository;

    public BuildingRepositoryImpl(BuildingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Building save(Building building) {
        return jpaRepository.save(building);
    }

    @Override
    public Optional<Building> findById(BuildingId id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Building> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(Building building) {
        jpaRepository.delete(building);
    }
}
