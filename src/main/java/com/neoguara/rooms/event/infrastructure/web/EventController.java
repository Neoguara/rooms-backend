package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Events", description = "Gerenciamento de eventos")
@RestController
@RequestMapping("/events")
public class EventController {

    private final GetEventUseCase getEventUseCase;

    EventController(GetEventUseCase getEventUseCase) {
        this.getEventUseCase = getEventUseCase;
    }

    @Operation(description = "Retorna todos os eventos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(getEventUseCase.findAll());
    }
}
