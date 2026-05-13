package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.CancelEventRequest;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequest;
import com.neoguara.rooms.event.application.usecases.ApproveEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import com.neoguara.rooms.event.application.usecases.RejectEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCreationUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCancellationUseCase;
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
    private final GetEventRequestUseCase getEventRequestUseCase;
    private final RequestEventCreationUseCase requestEventCreationUseCase;
    private final RequestEventUpdateUseCase requestEventUpdateUseCase;
    private final RequestEventCancellationUseCase requestEventCancellationUseCase;
    private final ApproveEventRequestUseCase approveEventRequestUseCase;
    private final RejectEventRequestUseCase rejectEventRequestUseCase;

    EventController(
            GetEventUseCase getEventUseCase,
            GetEventRequestUseCase getEventRequestUseCase,
            RequestEventCreationUseCase requestEventCreationUseCase,
            RequestEventUpdateUseCase requestEventUpdateUseCase,
            RequestEventCancellationUseCase requestEventCancellationUseCase,
            ApproveEventRequestUseCase approveEventRequestUseCase,
            RejectEventRequestUseCase rejectEventRequestUseCase
    ) {
        this.getEventUseCase = getEventUseCase;
        this.getEventRequestUseCase = getEventRequestUseCase;
        this.requestEventCreationUseCase = requestEventCreationUseCase;
        this.requestEventUpdateUseCase = requestEventUpdateUseCase;
        this.requestEventCancellationUseCase = requestEventCancellationUseCase;
        this.approveEventRequestUseCase = approveEventRequestUseCase;
        this.rejectEventRequestUseCase = rejectEventRequestUseCase;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<CreateEventRequestResponse> requestCancellation(
            @PathVariable UUID id,
            @RequestBody CancelEventRequest request
    ) {
        var response = requestEventCancellationUseCase.execute(id, request);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<EventRequestResponse>> findAllRequests() {
        return ResponseEntity.ok(getEventRequestUseCase.findAll());
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable UUID id) {
        approveEventRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        rejectEventRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
