package com.neoguara.rooms.room.application.dtos;

public record UpdateBuildingRequest(
        String name,
        String address,
        Integer totalFloors
) {}
