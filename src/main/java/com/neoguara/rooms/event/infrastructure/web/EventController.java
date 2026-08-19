package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Events", description = "Gerenciamento de eventos")
@RestController
@RequestMapping("/events")
public class EventController {

    private final GetEventUseCase getEventUseCase;

    EventController(GetEventUseCase getEventUseCase) {
        this.getEventUseCase = getEventUseCase;
    }

    @Operation(description = """
            Retorna todos os eventos cadastrados.
            Informando `seriesId`, retorna apenas as ocorrências daquela série recorrente, em ordem \
            cronológica. Cada ocorrência é um evento independente: pode ter sido editada ou \
            cancelada em separado das irmãs.""")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventResponse>> listEvents(
            @Parameter(description = "Filtra pelas ocorrências de uma série recorrente")
            @RequestParam(required = false) UUID seriesId) {
        return ResponseEntity.ok(
                seriesId == null ? getEventUseCase.findAll() : getEventUseCase.findBySeries(seriesId));
    }
}
