package com.neoguara.rooms.event.infrastructure.web;

import com.neoguara.rooms.event.application.dtos.EventRequestAuditResponse;
import com.neoguara.rooms.event.application.dtos.EventRequestResponse;
import com.neoguara.rooms.event.application.dtos.ReverseEventRequest;
import com.neoguara.rooms.event.application.dtos.ReviewEventRequest;
import com.neoguara.rooms.event.application.dtos.SubmitEventRequest;
import com.neoguara.rooms.event.application.usecases.GetEventRequestAuditUseCase;
import com.neoguara.rooms.event.application.usecases.GetEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.RequestEventChangesUseCase;
import com.neoguara.rooms.event.application.usecases.ReverseEventRequestUseCase;
import com.neoguara.rooms.event.application.usecases.ReviewEventRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.neoguara.rooms.auth.AuthUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Tag(name = "Event Requests", description = """
        Alterações em eventos não são aplicadas direto: são submetidas em grupo, decididas em grupo \
        e só então efetivadas.

        ## Ciclo de vida

        1. `POST /event-requests` registra um grupo com uma ou mais alterações. O grupo nasce \
        `PENDING`.
        2. `POST /event-requests/{id}/review` aprova ou rejeita **o grupo inteiro**. Aprovar \
        efetiva todas as alterações do grupo na mesma transação, na ordem em que foram submetidas; \
        rejeitar não altera nada.
        3. `GET /event-requests/{id}/audit` mostra as alterações do grupo e a decisão tomada.

        ## O grupo é a unidade de aprovação

        Não existe aprovar parte de um grupo: o status é `PENDING`, `APPROVED` ou `REJECTED`, sem \
        estado parcial. Quem submete decide o que é indivisível — alterações que precisam valer \
        juntas vão no mesmo grupo, alterações que podem ser decididas em separado vão em grupos \
        diferentes.

        Como consequência, se qualquer alteração do grupo não puder ser aplicada, a aprovação \
        inteira falha e nenhuma das outras é gravada.

        ## Estado exigido do evento

        Aprovar só funciona se cada evento estiver no estado compatível com o tipo da alteração. A \
        verificação acontece no momento da aprovação, não no da submissão:

        | Tipo | Exige o evento em |
        |---|---|
        | `CREATE` | — (o evento ainda não existe) |
        | `UPDATE` | `ACTIVE` |
        | `CANCEL` | `ACTIVE` |
        | `REACTIVATE` | `CANCELLED` |
        | `DISCARD` | `ACTIVE` ou `CANCELLED` |

        Por isso a ordem dentro do grupo importa: corrigir um evento cancelado exige `REACTIVATE` \
        antes de `UPDATE`. Os dois vão no mesmo grupo, nessa ordem.

        ## Decisões são definitivas

        Um grupo só pode ser decidido enquanto estiver `PENDING`. Decidir de novo — para reverter, \
        ou por outro revisor — responde `422 INVALID_STATE` e não altera nada.

        ## Como desfazer uma decisão errada

        A decisão em si nunca muda. Desfazer é `POST /event-requests/{id}/reverse`, que abre um \
        grupo novo com as alterações contrárias já montadas e o campo `reversalOf` apontando para \
        o grupo revertido. Esse grupo também precisa ser aprovado.

        | Alteração revertida | Alteração gerada | Efeito depois de aprovada |
        |---|---|---|
        | `CREATE` aprovado | `DISCARD` | evento vai para `DISCARDED` |
        | `UPDATE` aprovado | `UPDATE` com os valores `old*` | evento volta ao estado anterior |
        | `CANCEL` aprovado | `REACTIVATE` | evento volta para `ACTIVE` |
        | `REACTIVATE` aprovado | `CANCEL` | evento volta para `CANCELLED` |
        | grupo rejeitado | as alterações originais de novo | nova chance de decidir |

        Reverter um grupo aprovado gera as alterações contrárias **na ordem inversa**, porque \
        desfazer um grupo é desfazer por primeiro o que ele fez por último. Reverter um grupo \
        rejeitado repete as alterações originais na ordem original.

        Um grupo só pode ser revertido depois de decidido, e uma única vez. `DISCARDED` é \
        definitivo: para trazer de volta um evento descartado, submeta um `CREATE` novo.
        """)
@RestController
@RequestMapping("/event-requests")
public class EventRequestController {

    private final GetEventRequestUseCase getEventRequestUseCase;
    private final GetEventRequestAuditUseCase getEventRequestAuditUseCase;
    private final RequestEventChangesUseCase requestEventChangesUseCase;
    private final ReviewEventRequestUseCase reviewEventRequestUseCase;
    private final ReverseEventRequestUseCase reverseEventRequestUseCase;

    EventRequestController(
            GetEventRequestUseCase getEventRequestUseCase,
            GetEventRequestAuditUseCase getEventRequestAuditUseCase,
            RequestEventChangesUseCase requestEventChangesUseCase,
            ReviewEventRequestUseCase reviewEventRequestUseCase,
            ReverseEventRequestUseCase reverseEventRequestUseCase
    ) {
        this.getEventRequestUseCase = getEventRequestUseCase;
        this.getEventRequestAuditUseCase = getEventRequestAuditUseCase;
        this.requestEventChangesUseCase = requestEventChangesUseCase;
        this.reviewEventRequestUseCase = reviewEventRequestUseCase;
        this.reverseEventRequestUseCase = reverseEventRequestUseCase;
    }

    @Operation(description = "Retorna todos os grupos de solicitações com suas alterações.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<EventRequestResponse>> listEventRequests() {
        return ResponseEntity.ok(getEventRequestUseCase.findAll());
    }

    @Operation(description = """
            Submete um grupo de alterações de eventos. Criações, atualizações e cancelamentos podem \
            ser misturados na mesma lista `changes`, mas o grupo é decidido por inteiro depois: \
            alterações que precisam ser aprovadas ou rejeitadas em separado devem ir em grupos \
            diferentes.
            Campo obrigatório: `changes` (com ao menos um item). Campo opcional: `justification`. \
            O autor do grupo é o usuário autenticado, obtido do token.
            Cada item de `changes` é identificado pelo campo `type`:
            - `CREATE`: exige `title`, `startAt`, `endAt` (posterior a `startAt`) e `roomId`; não aceita `eventId`.
            - `UPDATE`: exige `eventId` e o estado completo desejado — `title`, `startAt`, `endAt` e `roomId` — \
            também os campos que não foram alterados.
            - `CANCEL`: exige apenas `eventId`.
            - `REACTIVATE`: exige apenas `eventId`. Devolve ao estado ativo um evento cancelado, \
            desfazendo um cancelamento indevido.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo registrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento referenciado por um item não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<EventRequestResponse> requestEventChanges(
            @AuthenticationPrincipal AuthUserDetails principal,
            @RequestBody SubmitEventRequest request) {
        var response = requestEventChangesUseCase.execute(principal.getId(), request);
        return ResponseEntity.created(URI.create("/event-requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Aprova ou rejeita o grupo inteiro. Aprovar efetiva todas as alterações do grupo \
            imediatamente, na ordem em que foram submetidas; rejeitar não altera nenhum evento. \
            Não é possível decidir parte de um grupo.
            Campo obrigatório: `decision`. Campo opcional: `comment`. Quem decide é o usuário \
            autenticado, obtido do token — não é possível decidir em nome de outra pessoa.
            A decisão é registrada no histórico de auditoria e nunca é sobrescrita.
            Aprovar exige que cada evento esteja no estado compatível com a alteração: `UPDATE` e \
            `CANCEL` só valem sobre eventos ativos, e `REACTIVATE` só sobre eventos cancelados. \
            Se uma alteração do grupo falhar, nenhuma das outras é gravada e o grupo continua \
            `PENDING`.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Decisão registrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo ou evento não encontrado"),
            @ApiResponse(responseCode = "422",
                    description = "Dados inválidos, grupo já decidido ou evento em estado incompatível")
    })
    @PostMapping("/{id}/review")
    public ResponseEntity<EventRequestResponse> reviewEventRequest(
            @AuthenticationPrincipal AuthUserDetails principal,
            @Parameter(description = "ID do grupo de solicitações") @PathVariable UUID id,
            @RequestBody ReviewEventRequest request) {
        return ResponseEntity.ok(reviewEventRequestUseCase.execute(id, principal.getId(), request));
    }

    @Operation(description = """
            Abre um grupo novo com as alterações que desfazem a decisão tomada sobre outro grupo, \
            derivadas automaticamente dos snapshots guardados. O grupo gerado começa `PENDING` e \
            passa pela mesma revisão de qualquer outro — reverter é uma solicitação, não um atalho.
            Não possui campos obrigatórios: o único campo do corpo, `justification`, é opcional. \
            O que será feito depende da decisão revertida.
            Reverter um grupo aprovado gera, na ordem inversa, a operação contrária de cada \
            alteração:
            - `CREATE` → `DISCARD` do evento criado;
            - `UPDATE` → `UPDATE` restaurando os valores `old*`;
            - `CANCEL` → `REACTIVATE`;
            - `REACTIVATE` → `CANCEL`.
            Reverter um grupo rejeitado repete as alterações originais, na ordem original, para \
            nova decisão.
            Só grupos já decididos podem ser revertidos, e cada grupo aceita uma única reversão.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo de reversão registrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo ou evento não encontrado"),
            @ApiResponse(responseCode = "422",
                    description = "Grupo ainda pendente, já revertido, ou evento descartado")
    })
    @PostMapping("/{id}/reverse")
    public ResponseEntity<EventRequestResponse> reverseEventRequest(
            @AuthenticationPrincipal AuthUserDetails principal,
            @Parameter(description = "ID do grupo cuja decisão será desfeita") @PathVariable UUID id,
            @RequestBody(required = false) ReverseEventRequest request) {
        var response = reverseEventRequestUseCase.execute(id, principal.getId(),
                request != null ? request : new ReverseEventRequest(null));
        return ResponseEntity.created(URI.create("/event-requests/" + response.id())).body(response);
    }

    @Operation(description = """
            Retorna o grupo com suas alterações e a trilha de auditoria da decisão tomada sobre \
            ele: qual foi, por quem, quando e com qual comentário.
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
