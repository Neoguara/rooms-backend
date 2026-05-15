package com.neoguara.rooms.room.infrastructure.web;

import com.neoguara.rooms.room.application.dtos.room.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.ReplaceRoomResourcesRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomDetailResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomExpandField;
import com.neoguara.rooms.room.application.dtos.room.RoomResourcesResponse;
import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomStatusRequest;
import com.neoguara.rooms.room.application.usecases.room.CreateRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.DeleteRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.GetRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.ReplaceRoomResourcesUseCase;
import com.neoguara.rooms.room.application.usecases.room.UpdateRoomStatusUseCase;
import com.neoguara.rooms.room.application.usecases.room.UpdateRoomUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private final UpdateRoomUseCase updateRoomUseCase;
    private final UpdateRoomStatusUseCase updateRoomStatusUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;
    private final ReplaceRoomResourcesUseCase replaceRoomResourcesUseCase;

    public RoomController(CreateRoomUseCase createRoomUseCase,
                          GetRoomUseCase getRoomUseCase,
                          UpdateRoomUseCase updateRoomUseCase,
                          UpdateRoomStatusUseCase updateRoomStatusUseCase,
                          DeleteRoomUseCase deleteRoomUseCase,
                          ReplaceRoomResourcesUseCase replaceRoomResourcesUseCase) {
        this.createRoomUseCase = createRoomUseCase;
        this.getRoomUseCase = getRoomUseCase;
        this.updateRoomUseCase = updateRoomUseCase;
        this.updateRoomStatusUseCase = updateRoomStatusUseCase;
        this.deleteRoomUseCase = deleteRoomUseCase;
        this.replaceRoomResourcesUseCase = replaceRoomResourcesUseCase;
    }

    @Operation(description = "Cadastra uma nova sala com status inicial ACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sala criada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoomUseCase.execute(request));
    }

    @Operation(description = "Retorna todas as salas cadastradas.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RoomDetailResponse>> listRooms(
            @Parameter(description = "Expandable fields: building, roomType, resources")
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
            @Parameter(description = "ID da sala") @PathVariable UUID id,
            @Parameter(description = "Expandable fields: building, roomType, resources")
            @RequestParam(required = false) List<String> expand) {
        return ResponseEntity.ok(getRoomUseCase.findById(id, parseExpand(expand)));
    }

    @Operation(description = "Atualiza os dados da sala.")
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

    @Operation(description = "Remove uma sala (soft delete). O status passa para DELETED e a sala deixa de ser visível.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sala removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Sala não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @Parameter(description = "ID da sala") @PathVariable UUID id) {
        deleteRoomUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Substitui todos os recursos da sala pela lista informada.")
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
                    Altera o status da sala. Transições permitidas:
                    - **AVAILABLE**: ativa uma sala INACTIVE ou MAINTENANCE. Se a sala estiver ARCHIVED, restaura para AVAILABLE.
                    - **INACTIVE**: desativa uma sala AVAILABLE ou MAINTENANCE. Inválido se ARCHIVED.
                    - **MAINTENANCE**: coloca a sala em manutenção. Inválido se ARCHIVED.
                    - **ARCHIVED**: arquiva a sala independente do status atual.

                    Transições inválidas retornam 422 (ex: tentar desativar ou colocar em manutenção uma sala ARCHIVED).
                    Para remover permanentemente uma sala use DELETE /rooms/{id}.
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
