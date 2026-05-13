package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.CancelEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequest;
import com.neoguara.rooms.event.application.dtos.CreateEventRequestResponse;
import com.neoguara.rooms.event.application.dtos.EventChangeRequest;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.EventResponse;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequest;
import com.neoguara.rooms.event.application.dtos.UpdateEventRequestStatusRequest;
import com.neoguara.rooms.event.application.usecases.ApproveEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventUseCase;
import com.neoguara.rooms.event.application.usecases.RejectEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCancellationUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventCreationUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventUpdateUseCase;
import com.neoguara.rooms.event.application.dtos.EventChangeType;
import com.neoguara.rooms.event.application.dtos.EventRequestStatusAction;
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

@Tag(name = "Events", description = "Gerenciamento de eventos")
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

    @Operation(description = "Retorna todos os eventos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventResponse>> findAll() {
        return ResponseEntity.ok(getEventUseCase.findAll());
    }

    @Operation(description = "Retorna todas as solicitações de eventos.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/requests")
    public ResponseEntity<List<EventRequestResponse>> findAllRequests() {
        return ResponseEntity.ok(getEventRequestUseCase.findAll());
    }

    @Operation(description = "Submete uma solicitação de criação de evento. Fica pendente até ser aprovada ou rejeitada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação de criação registrada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping("/requests")
    public ResponseEntity<CreateEventRequestResponse> requestCreation(@RequestBody CreateEventRequest request) {
        var response = requestEventCreationUseCase.execute(request);
        return ResponseEntity.created(URI.create("/events/requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Submete uma solicitação de alteração de um evento existente. Tipos aceitos no campo `type`:
            - **UPDATE**: atualiza os dados do evento. Requer todos os campos do evento.
            - **CANCEL**: solicita o cancelamento do evento. Requer apenas `userId` e `justification`.

            A solicitação fica pendente até ser aprovada ou rejeitada via `PATCH /events/requests/{id}`.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitação registrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos ou tipo de solicitação não suportado")
    })
    @PostMapping("/{id}/requests")
    public ResponseEntity<CreateEventRequestResponse> requestChange(
            @Parameter(description = "ID do evento") @PathVariable UUID id,
            @RequestBody EventChangeRequest request) {
        var response = switch (request.type()) {
            case UPDATE -> requestEventUpdateUseCase.execute(id, new UpdateEventRequest(
                    request.title(), request.description(), request.startAt(), request.endAt(),
                    request.isAllDay(), request.recurrenceRule(), request.justification(),
                    request.userId(), request.roomId()
            ));
            case CANCEL -> requestEventCancellationUseCase.execute(id, new CancelEventRequest(
                    request.userId(), request.justification()
            ));
        };
        return ResponseEntity.created(URI.create("/events/requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Atualiza o status de uma solicitação de evento. Valores aceitos:
            - **APPROVED**: aprova a solicitação, efetivando a criação, atualização ou cancelamento do evento.
            - **REJECTED**: rejeita a solicitação sem alterar o evento.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Solicitação não encontrada"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    @PatchMapping("/requests/{id}")
    public ResponseEntity<Void> updateRequestStatus(
            @Parameter(description = "ID da solicitação") @PathVariable UUID id,
            @RequestBody UpdateEventRequestStatusRequest request) {
        switch (request.status()) {
            case APPROVED -> approveEventRequestUseCase.execute(id);
            case REJECTED -> rejectEventRequestUseCase.execute(id);
        }
        return ResponseEntity.noContent().build();
    }
}
