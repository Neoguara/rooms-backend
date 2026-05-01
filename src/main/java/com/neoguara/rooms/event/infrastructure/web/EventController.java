package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequest;
import com.neoguara.rooms.event.application.usecases.ApproveEventChangeRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import com.neoguara.rooms.event.application.usecases.RejectEventChangeRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCreationUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventUpdateUseCase;
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
    private final RequestEventUpdateUseCase requestEventUpdateUseCase;
    private final ApproveEventChangeRequestUseCase approveEventChangeRequestUseCase;
    private final RejectEventChangeRequestUseCase rejectEventChangeRequestUseCase;

    EventController(
            GetEventUseCase getEventUseCase,
            RequestEventCreationUseCase requestEventCreationUseCase,
            RequestEventUpdateUseCase requestEventUpdateUseCase,
            ApproveEventChangeRequestUseCase approveEventChangeRequestUseCase,
            RejectEventChangeRequestUseCase rejectEventChangeRequestUseCase
    ) {
        this.getEventUseCase = getEventUseCase;
        this.requestEventCreationUseCase = requestEventCreationUseCase;
        this.requestEventUpdateUseCase = requestEventUpdateUseCase;
        this.approveEventChangeRequestUseCase = approveEventChangeRequestUseCase;
        this.rejectEventChangeRequestUseCase = rejectEventChangeRequestUseCase;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(getEventUseCase.findAll());
    }

    @PostMapping
    public ResponseEntity<CreateEventRequestResponse> requestCreation(@RequestBody CreateEventRequest request) {
        var response = requestEventCreationUseCase.execute(request);
        return ResponseEntity.created(URI.create("/events/requests/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreateEventRequestResponse> requestUpdate(
            @PathVariable UUID id,
            @RequestBody UpdateEventRequest request
    ) {
        var response = requestEventUpdateUseCase.execute(id, request);
        return ResponseEntity.created(URI.create("/events/requests/" + response.id())).body(response);
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        approveEventChangeRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        rejectEventChangeRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
