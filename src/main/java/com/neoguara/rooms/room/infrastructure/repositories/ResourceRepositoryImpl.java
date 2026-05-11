package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.application.ports.ResourceRepositoryPort;
import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResourceRepositoryImpl implements ResourceRepositoryPort {

    private final ResourceJpaRepository jpaRepository;

    public ResourceRepositoryImpl(ResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Resource save(Resource resource) {
        return jpaRepository.save(resource);
    }

    @Override
    public Optional<Resource> findById(ResourceId id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Resource> findAllById(List<ResourceId> ids) {
        return jpaRepository.findAllById(ids);
    }

    @Override
    public List<Resource> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(Resource resource) {
        jpaRepository.delete(resource);
    }
}
