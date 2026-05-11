package com.neoguara.rooms.room.application.dtos.room;

public enum RoomExpandField {
    BUILDING, ROOM_TYPE, RESOURCES;

    public static RoomExpandField fromString(String value) {
        return switch (value.toLowerCase()) {
            case "building" -> BUILDING;
            case "roomtype", "room_type" -> ROOM_TYPE;
            case "resources" -> RESOURCES;
            default -> throw new IllegalArgumentException("Unknown expand field: " + value);
        };
    }
}
