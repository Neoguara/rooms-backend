package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.CancelEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequest;
import com.neoguara.rooms.event.application.usecases.ApproveEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RejectEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCancellationUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCreationUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventUpdateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Event Requests", description = "Solicitações de eventos")
@RestController
@RequestMapping("/event-requests")
public class EventRequestController {

    private final GetEventRequestUseCase getEventRequestUseCase;
    private final RequestEventCreationUseCase requestEventCreationUseCase;
    private final RequestEventUpdateUseCase requestEventUpdateUseCase;
    private final RequestEventCancellationUseCase requestEventCancellationUseCase;
    private final ApproveEventRequestUseCase approveEventRequestUseCase;
    private final RejectEventRequestUseCase rejectEventRequestUseCase;

    EventRequestController(
            GetEventRequestUseCase getEventRequestUseCase,
            RequestEventCreationUseCase requestEventCreationUseCase,
            RequestEventUpdateUseCase requestEventUpdateUseCase,
            RequestEventCancellationUseCase requestEventCancellationUseCase,
            ApproveEventRequestUseCase approveEventRequestUseCase,
            RejectEventRequestUseCase rejectEventRequestUseCase
    ) {
        this.getEventRequestUseCase = getEventRequestUseCase;
        this.requestEventCreationUseCase = requestEventCreationUseCase;
        this.requestEventUpdateUseCase = requestEventUpdateUseCase;
        this.requestEventCancellationUseCase = requestEventCancellationUseCase;
        this.approveEventRequestUseCase = approveEventRequestUseCase;
        this.rejectEventRequestUseCase = rejectEventRequestUseCase;
    }

    @Operation(description = "Retorna todas as solicitações de eventos.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventRequestResponse>> listEventRequests() {
        return ResponseEntity.ok(getEventRequestUseCase.findAll());
    }

    @Operation(description = """
            Solicita a criação de um novo evento. Fica pendente até ser aprovada ou rejeitada.
            Campos obrigatórios: `title`, `startAt`, `endAt` (posterior a `startAt`), `userId` e `roomId`.
            Campos opcionais: `description`, `isAllDay`, `recurrenceRule` e `justification`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping("/create-event")
    public ResponseEntity<CreateEventRequestResponse> requestEventCreation(@RequestBody CreateEventRequest request) {
        var response = requestEventCreationUseCase.execute(request);
        return ResponseEntity.created(URI.create("/event-requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Solicita a atualização de um evento existente. Fica pendente até ser aprovada ou rejeitada.
            A solicitação descreve o estado completo desejado do evento.
            Campos obrigatórios: `eventId`, `title`, `startAt`, `endAt` (posterior a `startAt`), `userId` e `roomId` — \
            também os que não foram alterados.
            Campos opcionais: `description`, `isAllDay`, `recurrenceRule` e `justification`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping("/update-event")
    public ResponseEntity<CreateEventRequestResponse> requestEventUpdate(@RequestBody UpdateEventRequest request) {
        var response = requestEventUpdateUseCase.execute(request.eventId(), request);
        return ResponseEntity.created(URI.create("/event-requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Solicita o cancelamento de um evento existente. Fica pendente até ser aprovada ou rejeitada.
            Campos obrigatórios: `eventId` e `userId`.
            Campo opcional: `justification`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping("/cancel-event")
    public ResponseEntity<CreateEventRequestResponse> requestEventCancellation(@RequestBody CancelEventRequest request) {
        var response = requestEventCancellationUseCase.execute(request.eventId(), request);
        return ResponseEntity.created(URI.create("/event-requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Aprova uma solicitação, efetivando a operação sobre o evento.
            Não possui corpo: o único dado obrigatório é o `id` da solicitação, informado na URL.""")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação aprovada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "422", description = "Solicitação já foi processada")
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveEventRequest(
            @Parameter(description = "ID da solicitação") @PathVariable UUID id) {
        approveEventRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = """
            Rejeita uma solicitação sem alterar o evento.
            Não possui corpo: o único dado obrigatório é o `id` da solicitação, informado na URL.""")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitação rejeitada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "422", description = "Solicitação já foi processada")
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectEventRequest(
            @Parameter(description = "ID da solicitação") @PathVariable UUID id) {
        rejectEventRequestUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
