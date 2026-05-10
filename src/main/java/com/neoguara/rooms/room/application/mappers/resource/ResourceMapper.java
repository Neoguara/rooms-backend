package com.neoguara.rooms.room.application.mappers.resource;

import com.neoguara.rooms.room.application.dtos.resource.CreateResourceRequest;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.domain.entities.Resource;

public class ResourceMapper {

    private ResourceMapper() {}

    public static Resource toDomain(CreateResourceRequest request) {
        return Resource.create(request.name(), request.description(), request.icon());
    }

    public static ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId().id(),
                resource.getName(),
                resource.getDescription(),
                resource.getIcon(),
                resource.getActive(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }
}
