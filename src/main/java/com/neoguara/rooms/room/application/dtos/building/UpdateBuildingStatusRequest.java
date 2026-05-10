package com.neoguara.rooms.room.application.dtos.building;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;

public record UpdateBuildingStatusRequest(BuildingStatus status) {}
