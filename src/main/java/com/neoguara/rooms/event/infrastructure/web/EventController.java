package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.usecases.ApproveEventChangeRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCreationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final GetEventUseCase getEventUseCase;
    private final RequestEventCreationUseCase requestEventCreationUseCase;
    private final ApproveEventChangeRequestUseCase approveEventChangeRequestUseCase;

    EventController(
            GetEventUseCase getEventUseCase,
            RequestEventCreationUseCase requestEventCreationUseCase,
            ApproveEventChangeRequestUseCase approveEventChangeRequestUseCase
    ) {
        this.getEventUseCase = getEventUseCase;
        this.requestEventCreationUseCase = requestEventCreationUseCase;
        this.approveEventChangeRequestUseCase = approveEventChangeRequestUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(getEventUseCase.findAll());
    }

    @PostMapping("/requests")
    public ResponseEntity<CreateEventRequestResponse> requestCreation(@RequestBody CreateEventRequest request) {
        var response = requestEventCreationUseCase.execute(request);
        return ResponseEntity.created(URI.create("/events/requests/" + response.id())).body(response);
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        approveEventChangeRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
