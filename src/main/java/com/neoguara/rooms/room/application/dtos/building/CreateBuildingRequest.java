package com.neoguara.rooms.room.application.dtos.building;

public record CreateBuildingRequest(
        String name,
        String address,
        Integer totalFloors
) {}
