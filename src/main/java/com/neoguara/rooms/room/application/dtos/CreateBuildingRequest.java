package com.neoguara.rooms.room.application.dtos;

public record CreateBuildingRequest(
        String name,
        String address,
        Integer totalFloors
) {}
