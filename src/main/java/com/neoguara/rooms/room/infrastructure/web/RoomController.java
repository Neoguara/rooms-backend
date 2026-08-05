package com.neoguara.rooms.room.infrastructure.web;

import com.neoguara.rooms.room.application.dtos.room.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.ReplaceRoomResourcesRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomAvailabilityFilter;
import com.neoguara.rooms.room.application.dtos.room.RoomDetailResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomExpandField;
import com.neoguara.rooms.room.application.dtos.room.RoomResourcesResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomStatusRequest;
import com.neoguara.rooms.room.application.usecases.room.CreateRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.DeleteRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.GetAvailableRoomsUseCase;
import com.neoguara.rooms.room.application.usecases.room.GetRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.ReplaceRoomResourcesUseCase;
import com.neoguara.rooms.room.application.usecases.room.UpdateRoomStatusUseCase;
import com.neoguara.rooms.room.application.usecases.room.UpdateRoomUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Rooms", description = "Gerenciamento de salas")
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final CreateRoomUseCase createRoomUseCase;
    private final GetRoomUseCase getRoomUseCase;
    private final GetAvailableRoomsUseCase getAvailableRoomsUseCase;
    private final UpdateRoomUseCase updateRoomUseCase;
    private final UpdateRoomStatusUseCase updateRoomStatusUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;
    private final ReplaceRoomResourcesUseCase replaceRoomResourcesUseCase;

    public RoomController(CreateRoomUseCase createRoomUseCase,
                          GetRoomUseCase getRoomUseCase,
                          GetAvailableRoomsUseCase getAvailableRoomsUseCase,
                          UpdateRoomUseCase updateRoomUseCase,
                          UpdateRoomStatusUseCase updateRoomStatusUseCase,
                          DeleteRoomUseCase deleteRoomUseCase,
                          ReplaceRoomResourcesUseCase replaceRoomResourcesUseCase) {
        this.createRoomUseCase = createRoomUseCase;
        this.getRoomUseCase = getRoomUseCase;
        this.getAvailableRoomsUseCase = getAvailableRoomsUseCase;
        this.updateRoomUseCase = updateRoomUseCase;
        this.updateRoomStatusUseCase = updateRoomStatusUseCase;
        this.deleteRoomUseCase = deleteRoomUseCase;
        this.replaceRoomResourcesUseCase = replaceRoomResourcesUseCase;
    }

    @Operation(description = """
            Cadastra uma nova sala com status inicial AVAILABLE.
            Campos obrigatórios: `name`, `code`, `roomTypeId`, `buildingId`, `floor` (zero ou maior) e `capacity` (mínimo 1).
            Campo opcional: `resourceIds` — quando omitido, a sala é criada sem recursos.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sala criada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoomUseCase.execute(request));
    }

    @Operation(description = """
            Retorna todas as salas disponíveis no período informado.
            Os parâmetros `startAt` e `endAt` são obrigatórios; todos os demais são opcionais.
            Filtros opcionais por tipo de sala, prédio, recursos e capacidade mínima.
            O parâmetro `search` realiza busca textual (case-insensitive) nos campos:
            nome e código da sala, nome e descrição do tipo de sala, nome do prédio,
            nome e descrição dos recursos associados.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Salas disponíveis retornadas"),
            @ApiResponse(responseCode = "422", description = "Período inválido (startAt >= endAt ou ausente)")
    })
    @GetMapping("/available")
    public ResponseEntity<List<RoomDetailResponse>> getAvailableRooms(
            @Parameter(description = "Obrigatório. Início do período (ISO-8601, ex: 2024-06-01T09:00:00)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @Parameter(description = "Obrigatório. Fim do período (ISO-8601, ex: 2024-06-01T11:00:00)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
            @Parameter(description = "Opcional. Filtrar por tipos de sala (lista de UUIDs)", required = false) @RequestParam(required = false) List<UUID> roomTypeIds,
            @Parameter(description = "Opcional. Filtrar por recursos que a sala deve ter (lista de UUIDs)", required = false) @RequestParam(required = false) List<UUID> resourceIds,
            @Parameter(description = "Opcional. Filtrar por prédios (lista de UUIDs)", required = false) @RequestParam(required = false) List<UUID> buildingIds,
            @Parameter(description = "Opcional. Capacidade mínima da sala", required = false) @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Opcional. Busca textual: nome/código da sala, nome/descrição do tipo, nome do prédio, nome/descrição dos recursos", required = false) @RequestParam(required = false) String search,
            @Parameter(description = "Opcional. Campos expandidos na resposta: building, roomType, resources", required = false) @RequestParam(required = false) List<String> expand) {
        RoomAvailabilityFilter filter = new RoomAvailabilityFilter(startAt, endAt, roomTypeIds, resourceIds, buildingIds, minCapacity, search);
        return ResponseEntity.ok(getAvailableRoomsUseCase.execute(filter, parseExpand(expand)));
    }

    @Operation(description = "Retorna todas as salas cadastradas. Não possui parâmetros obrigatórios.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RoomDetailResponse>> listRooms(
            @Parameter(description = "Opcional. Campos expandidos na resposta: building, roomType, resources", required = false)
            @RequestParam(required = false) List<String> expand) {
        return ResponseEntity.ok(getRoomUseCase.findAll(parseExpand(expand)));
    }

    @Operation(description = "Retorna os dados de uma sala pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sala encontrada"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailResponse> getRoom(
            @Parameter(description = "Obrigatório. ID da sala", required = true) @PathVariable UUID id,
            @Parameter(description = "Opcional. Campos expandidos na resposta: building, roomType, resources", required = false)
            @RequestParam(required = false) List<String> expand) {
        return ResponseEntity.ok(getRoomUseCase.findById(id, parseExpand(expand)));
    }

    @Operation(description = """
            Atualiza os dados da sala.
            A atualização substitui todos os campos: `name`, `code`, `roomTypeId`, `buildingId`, `floor` e `capacity` são obrigatórios.
            Não há campos opcionais — campos omitidos são tratados como nulos (ou zero) e resultam em 422.
            Os recursos da sala são alterados em PUT /rooms/{id}/resources.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sala atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @Parameter(description = "ID da sala") @PathVariable UUID id,
            @RequestBody UpdateRoomRequest request) {
        return ResponseEntity.ok(updateRoomUseCase.execute(id, request));
    }

    @Operation(description = "Remove uma sala (soft delete). A sala deve estar com status INACTIVE antes de ser deletada; caso contrário retorna 422.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sala removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada"),
            @ApiResponse(responseCode = "422", description = "Sala não está inativa")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "ID da sala") @PathVariable UUID id) {
        deleteRoomUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = """
            Substitui todos os recursos da sala pela lista informada.
            Campo obrigatório: `resourceIds` — envie uma lista vazia para remover todos os recursos.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recursos atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala ou recurso não encontrado")
    })
    @PutMapping("/{id}/resources")
    public ResponseEntity<RoomResourcesResponse> replaceRoomResources(
            @Parameter(description = "ID da sala") @PathVariable UUID id,
            @RequestBody ReplaceRoomResourcesRequest request) {
        return ResponseEntity.ok(replaceRoomResourcesUseCase.execute(id, request));
    }

    @Operation(
            description = """
                    Altera o status da sala. Campo obrigatório: `status`. Transições permitidas:
                    - **AVAILABLE**: ativa uma sala INACTIVE ou MAINTENANCE.
                    - **INACTIVE**: desativa uma sala AVAILABLE ou MAINTENANCE.
                    - **MAINTENANCE**: coloca a sala em manutenção.

                    Transições inválidas retornam 422 (ex: alterar o status de uma sala já deletada).
                    Para remover uma sala use DELETE /rooms/{id} — ela precisa estar INACTIVE antes.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomResponse> updateRoomStatus(
            @Parameter(description = "ID da sala") @PathVariable UUID id,
            @RequestBody UpdateRoomStatusRequest request) {
        return ResponseEntity.ok(updateRoomStatusUseCase.execute(id, request.status()));
    }

    private Set<RoomExpandField> parseExpand(List<String> expand) {
        if (expand == null || expand.isEmpty()) return Set.of();
        return expand.stream()
                .map(RoomExpandField::fromString)
                .collect(Collectors.toSet());
    }
}
