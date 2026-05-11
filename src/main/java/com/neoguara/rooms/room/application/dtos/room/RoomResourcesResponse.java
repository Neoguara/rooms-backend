package com.neoguara.rooms.room.application.dtos.room;

import java.util.List;
import java.util.UUID;

public record RoomResourcesResponse(UUID roomId, List<UUID> resourceIds) {}
