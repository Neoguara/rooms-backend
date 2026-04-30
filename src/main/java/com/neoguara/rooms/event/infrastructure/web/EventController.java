package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final GetEventUseCase getEventUseCase;

    EventController(GetEventUseCase getEventUseCase) {
        this.getEventUseCase = getEventUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(getEventUseCase.findAll());
    }
}
