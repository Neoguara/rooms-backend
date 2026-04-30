package com.neoguara.rooms.event.application.ports;

import com.neoguara.rooms.event.domain.entities.Event;

import java.util.List;

public interface EventRepositoryPort {
    List<Event> findAll();
}
