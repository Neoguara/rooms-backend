package com.neoguara.rooms.room.infrastructure.web;

import com.neoguara.rooms.room.application.dtos.roomtype.CreateRoomTypeRequest;
import com.neoguara.rooms.room.application.dtos.roomtype.RoomTypeResponse;
import com.neoguara.rooms.room.application.dtos.roomtype.UpdateRoomTypeRequest;
import com.neoguara.rooms.room.application.dtos.roomtype.UpdateRoomTypeStatusRequest;
import com.neoguara.rooms.room.application.usecases.roomtype.CreateRoomTypeUseCase;
import com.neoguara.rooms.room.application.usecases.roomtype.DeleteRoomTypeUseCase;
import com.neoguara.rooms.room.application.usecases.roomtype.GetRoomTypeUseCase;
import com.neoguara.rooms.room.application.usecases.roomtype.UpdateRoomTypeStatusUseCase;
import com.neoguara.rooms.room.application.usecases.roomtype.UpdateRoomTypeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Room Types", description = "Gerenciamento de tipos de sala")
@RestController
@RequestMapping("/room-types")
public class RoomTypeController {

    private final CreateRoomTypeUseCase createRoomTypeUseCase;
    private final GetRoomTypeUseCase getRoomTypeUseCase;
    private final UpdateRoomTypeUseCase updateRoomTypeUseCase;
    private final UpdateRoomTypeStatusUseCase updateRoomTypeStatusUseCase;
    private final DeleteRoomTypeUseCase deleteRoomTypeUseCase;

    public RoomTypeController(CreateRoomTypeUseCase createRoomTypeUseCase,
                              GetRoomTypeUseCase getRoomTypeUseCase,
                              UpdateRoomTypeUseCase updateRoomTypeUseCase,
                              UpdateRoomTypeStatusUseCase updateRoomTypeStatusUseCase,
                              DeleteRoomTypeUseCase deleteRoomTypeUseCase) {
        this.createRoomTypeUseCase = createRoomTypeUseCase;
        this.getRoomTypeUseCase = getRoomTypeUseCase;
        this.updateRoomTypeUseCase = updateRoomTypeUseCase;
        this.updateRoomTypeStatusUseCase = updateRoomTypeStatusUseCase;
        this.deleteRoomTypeUseCase = deleteRoomTypeUseCase;
    }

    @Operation(description = "Cadastra um novo tipo de sala.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de sala criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<RoomTypeResponse> createRoomType(@RequestBody CreateRoomTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoomTypeUseCase.execute(request));
    }

    @Operation(description = "Retorna todos os tipos de sala cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<RoomTypeResponse>> listRoomTypes() {
        return ResponseEntity.ok(getRoomTypeUseCase.findAll());
    }

    @Operation(description = "Retorna os dados de um tipo de sala pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de sala encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de sala não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeResponse> getRoomType(
            @Parameter(description = "ID do tipo de sala") @PathVariable UUID id) {
        return ResponseEntity.ok(getRoomTypeUseCase.findById(id));
    }

    @Operation(description = "Atualiza os dados de um tipo de sala.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de sala atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de sala não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeResponse> updateRoomType(
            @Parameter(description = "ID do tipo de sala") @PathVariable UUID id,
            @RequestBody UpdateRoomTypeRequest request) {
        return ResponseEntity.ok(updateRoomTypeUseCase.execute(id, request));
    }

    @Operation(
            description = """
                    Altera o status do tipo de sala. Transições permitidas:
                    - **ACTIVE**: ativa um tipo de sala INACTIVE, ou restaura um ARCHIVED.
                    - **INACTIVE**: desativa um tipo de sala ACTIVE.
                    - **ARCHIVED**: arquiva o tipo de sala independente do status atual.

                    Transições inválidas retornam 422 (ex: ativar diretamente um tipo ARCHIVED sem restaurar).
                    Para remover permanentemente use DELETE /room-types/{id} (exige ARCHIVED).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de sala não encontrado"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<RoomTypeResponse> updateRoomTypeStatus(
            @Parameter(description = "ID do tipo de sala") @PathVariable UUID id,
            @RequestBody UpdateRoomTypeStatusRequest request) {
        return ResponseEntity.ok(updateRoomTypeStatusUseCase.execute(id, request.status()));
    }

    @Operation(description = "Remove um tipo de sala (soft delete). O tipo de sala deve estar com status ARCHIVED antes de ser deletado; caso contrário retorna 422.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de sala removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de sala não encontrado"),
            @ApiResponse(responseCode = "422", description = "Tipo de sala não está arquivado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(
            @Parameter(description = "ID do tipo de sala") @PathVariable UUID id) {
        deleteRoomTypeUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
