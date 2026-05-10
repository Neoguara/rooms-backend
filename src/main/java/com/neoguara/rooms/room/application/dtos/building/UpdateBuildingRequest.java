package com.neoguara.rooms.room.application.dtos.building;

public record UpdateBuildingRequest(
        String name,
        String address,
        Integer totalFloors
) {}
