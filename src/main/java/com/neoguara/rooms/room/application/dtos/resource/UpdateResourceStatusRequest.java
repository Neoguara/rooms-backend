package com.neoguara.rooms.room.application.dtos.resource;

import com.neoguara.rooms.room.domain.enums.ResourceStatus;

public record UpdateResourceStatusRequest(ResourceStatus status) {}
