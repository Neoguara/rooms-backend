package com.neoguara.rooms.room.application.ports;

import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;

import java.util.List;
import java.util.Optional;

public interface ResourceRepositoryPort {
    Resource save(Resource resource);
    Optional<Resource> findById(ResourceId id);
    List<Resource> findAll();
    void delete(Resource resource);
}
