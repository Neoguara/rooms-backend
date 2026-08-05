package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.EventRequestAuditResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.ReviewEventRequest;
import com.neoguara.rooms.event.application.dtos.SubmitEventRequest;
import com.neoguara.rooms.event.application.usecases.GetEventRequestAuditUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventChangesUseCase;
import com.neoguara.rooms.event.application.usecases.ReviewEventRequestUseCase;
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
    private final GetEventRequestAuditUseCase getEventRequestAuditUseCase;
    private final RequestEventChangesUseCase requestEventChangesUseCase;
    private final ReviewEventRequestUseCase reviewEventRequestUseCase;

    EventRequestController(
            GetEventRequestUseCase getEventRequestUseCase,
            GetEventRequestAuditUseCase getEventRequestAuditUseCase,
            RequestEventChangesUseCase requestEventChangesUseCase,
            ReviewEventRequestUseCase reviewEventRequestUseCase
    ) {
        this.getEventRequestUseCase = getEventRequestUseCase;
        this.getEventRequestAuditUseCase = getEventRequestAuditUseCase;
        this.requestEventChangesUseCase = requestEventChangesUseCase;
        this.reviewEventRequestUseCase = reviewEventRequestUseCase;
    }

    @Operation(description = "Retorna todos os grupos de solicitações com suas alterações.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventRequestResponse>> listEventRequests() {
        return ResponseEntity.ok(getEventRequestUseCase.findAll());
    }

    @Operation(description = """
            Submete um grupo de alterações de eventos. Criações, atualizações e cancelamentos podem \
            ser misturados na mesma lista `changes`, e cada alteração é aprovada ou rejeitada \
            individualmente depois.
            Campos obrigatórios: `userId` e `changes` (com ao menos um item). Campo opcional: `justification`.
            Cada item de `changes` é identificado pelo campo `type`:
            - `CREATE`: exige `title`, `startAt`, `endAt` (posterior a `startAt`) e `roomId`; não aceita `eventId`.
            - `UPDATE`: exige `eventId` e o estado completo desejado — `title`, `startAt`, `endAt` e `roomId` — \
            também os campos que não foram alterados.
            - `CANCEL`: exige apenas `eventId`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo registrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento referenciado por um item não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<EventRequestResponse> requestEventChanges(@RequestBody SubmitEventRequest request) {
        var response = requestEventChangesUseCase.execute(request);
        return ResponseEntity.created(URI.create("/event-requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Aprova ou rejeita alterações do grupo, uma a uma, em uma única chamada. Alterações \
            aprovadas são efetivadas imediatamente sobre os eventos; alterações rejeitadas não \
            alteram nada. Itens que não aparecerem em `decisions` continuam pendentes, e o status \
            do grupo é recalculado a partir dos itens.
            Campos obrigatórios: `reviewedBy` e `decisions` (com ao menos um item, cada um com \
            `itemId` e `decision`). Campo opcional por item: `comment`.
            Cada decisão é registrada no histórico de auditoria e nunca é sobrescrita.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Decisões registradas com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo, item de alteração ou evento não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos ou item já decidido")
    })
    @PostMapping("/{id}/review")
    public ResponseEntity<EventRequestResponse> reviewEventRequest(
            @Parameter(description = "ID do grupo de solicitações") @PathVariable UUID id,
            @RequestBody ReviewEventRequest request) {
        return ResponseEntity.ok(reviewEventRequestUseCase.execute(id, request));
    }

    @Operation(description = """
            Retorna o grupo com a trilha de auditoria de cada alteração: todas as decisões tomadas, \
            por quem, quando e com qual comentário, da mais antiga para a mais recente.
            Não possui corpo: o único dado obrigatório é o `id` do grupo, informado na URL.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado")
    })
    @GetMapping("/{id}/audit")
    public ResponseEntity<EventRequestAuditResponse> getEventRequestAudit(
            @Parameter(description = "ID do grupo de solicitações") @PathVariable UUID id) {
        return ResponseEntity.ok(getEventRequestAuditUseCase.execute(id));
    }
}
