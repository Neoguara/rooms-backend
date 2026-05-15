package com.neoguara.rooms.user.application.dtos;

public record UpdateUserStatusRequest(Status status) {
    public enum Status { ACTIVE, INACTIVE }
}
