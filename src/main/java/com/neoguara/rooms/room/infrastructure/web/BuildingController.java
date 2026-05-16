package com.neoguara.rooms.room.infrastructure.web;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.building.CreateBuildingRequest;
import com.neoguara.rooms.room.application.dtos.building.UpdateBuildingRequest;
import com.neoguara.rooms.room.application.dtos.building.UpdateBuildingStatusRequest;
import com.neoguara.rooms.room.application.usecases.building.CreateBuildingUseCase;
import com.neoguara.rooms.room.application.usecases.building.DeleteBuildingUseCase;
import com.neoguara.rooms.room.application.usecases.building.GetBuildingUseCase;
import com.neoguara.rooms.room.application.usecases.building.UpdateBuildingStatusUseCase;
import com.neoguara.rooms.room.application.usecases.building.UpdateBuildingUseCase;
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

@Tag(name = "Buildings", description = "Gerenciamento de edifícios")
@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private final CreateBuildingUseCase createBuildingUseCase;
    private final GetBuildingUseCase getBuildingUseCase;
    private final UpdateBuildingUseCase updateBuildingUseCase;
    private final UpdateBuildingStatusUseCase updateBuildingStatusUseCase;
    private final DeleteBuildingUseCase deleteBuildingUseCase;

    public BuildingController(CreateBuildingUseCase createBuildingUseCase,
                              GetBuildingUseCase getBuildingUseCase,
                              UpdateBuildingUseCase updateBuildingUseCase,
                              UpdateBuildingStatusUseCase updateBuildingStatusUseCase,
                              DeleteBuildingUseCase deleteBuildingUseCase) {
        this.createBuildingUseCase = createBuildingUseCase;
        this.getBuildingUseCase = getBuildingUseCase;
        this.updateBuildingUseCase = updateBuildingUseCase;
        this.updateBuildingStatusUseCase = updateBuildingStatusUseCase;
        this.deleteBuildingUseCase = deleteBuildingUseCase;
    }

    @Operation(description = "Cadastra um novo edifício com status inicial ACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Edifício criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos (nome, endereço ou andares ausentes/inválidos)")
    })
    @PostMapping
    public ResponseEntity<BuildingResponse> createBuilding(@RequestBody CreateBuildingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBuildingUseCase.execute(request));
    }

    @Operation(description = "Retorna todos os edifícios cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<BuildingResponse>> listBuildings() {
        return ResponseEntity.ok(getBuildingUseCase.findAll());
    }

    @Operation(description = "Retorna os dados de um edifício pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Edifício encontrado"),
            @ApiResponse(responseCode = "404", description = "Edifício não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponse> getBuilding(
            @Parameter(description = "ID do edifício") @PathVariable UUID id) {
        return ResponseEntity.ok(getBuildingUseCase.findById(id));
    }

    @Operation(description = "Atualiza nome, endereço e total de andares do edifício.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Edifício atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Edifício não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponse> updateBuilding(
            @Parameter(description = "ID do edifício") @PathVariable UUID id,
            @RequestBody UpdateBuildingRequest request) {
        return ResponseEntity.ok(updateBuildingUseCase.execute(id, request));
    }

    @Operation(description = "Remove um edifício (soft delete). O edifício deve estar com status ARCHIVED antes de ser deletado; caso contrário retorna 422.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Edifício removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Edifício não encontrado"),
            @ApiResponse(responseCode = "422", description = "Edifício não está arquivado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(
            @Parameter(description = "ID do edifício") @PathVariable UUID id) {
        deleteBuildingUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            description = """
                    Altera o status do edifício. Transições permitidas:
                    - **ACTIVE**: ativa um edifício INACTIVE, ou restaura um ARCHIVED.
                    - **INACTIVE**: desativa um edifício ACTIVE.
                    - **ARCHIVED**: arquiva o edifício independente do status atual.

                    Transições inválidas retornam 422 (ex: ativar um edifício já ARCHIVED diretamente).
                    Para remover permanentemente um edifício use DELETE /buildings/{id}.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Edifício não encontrado"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<BuildingResponse> updateBuildingStatus(
            @Parameter(description = "ID do edifício") @PathVariable UUID id,
            @RequestBody UpdateBuildingStatusRequest request) {
        return ResponseEntity.ok(updateBuildingStatusUseCase.execute(id, request.status()));
    }
}
