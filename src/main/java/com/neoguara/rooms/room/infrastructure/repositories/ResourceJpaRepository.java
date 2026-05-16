package com.neoguara.rooms.room.infrastructure.repositories;

import com.neoguara.rooms.room.domain.entities.Resource;
import com.neoguara.rooms.room.domain.enums.ResourceStatus;
import com.neoguara.rooms.room.domain.valueobjects.ResourceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ResourceJpaRepository extends JpaRepository<Resource, ResourceId> {
    List<Resource> findAllByStatusNot(ResourceStatus status);
    Optional<Resource> findByIdAndStatusNot(ResourceId id, ResourceStatus status);
    List<Resource> findAllByIdInAndStatusNot(Collection<ResourceId> ids, ResourceStatus status);
}
