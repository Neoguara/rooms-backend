package com.neoguara.rooms.room.infrastructure.web;

import com.neoguara.rooms.room.application.dtos.resource.CreateResourceRequest;
import com.neoguara.rooms.room.application.dtos.resource.ResourceResponse;
import com.neoguara.rooms.room.application.dtos.resource.UpdateResourceRequest;
import com.neoguara.rooms.room.application.dtos.resource.UpdateResourceStatusRequest;
import com.neoguara.rooms.room.application.usecases.resource.CreateResourceUseCase;
import com.neoguara.rooms.room.application.usecases.resource.DeleteResourceUseCase;
import com.neoguara.rooms.room.application.usecases.resource.GetResourceUseCase;
import com.neoguara.rooms.room.application.usecases.resource.UpdateResourceStatusUseCase;
import com.neoguara.rooms.room.application.usecases.resource.UpdateResourceUseCase;
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

@Tag(name = "Resources", description = "Gerenciamento de recursos de salas")
@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final CreateResourceUseCase createResourceUseCase;
    private final GetResourceUseCase getResourceUseCase;
    private final UpdateResourceUseCase updateResourceUseCase;
    private final UpdateResourceStatusUseCase updateResourceStatusUseCase;
    private final DeleteResourceUseCase deleteResourceUseCase;

    public ResourceController(CreateResourceUseCase createResourceUseCase,
                              GetResourceUseCase getResourceUseCase,
                              UpdateResourceUseCase updateResourceUseCase,
                              UpdateResourceStatusUseCase updateResourceStatusUseCase,
                              DeleteResourceUseCase deleteResourceUseCase) {
        this.createResourceUseCase = createResourceUseCase;
        this.getResourceUseCase = getResourceUseCase;
        this.updateResourceUseCase = updateResourceUseCase;
        this.updateResourceStatusUseCase = updateResourceStatusUseCase;
        this.deleteResourceUseCase = deleteResourceUseCase;
    }

    @Operation(description = """
            Cadastra um novo recurso.
            Campos obrigatórios: `name`, `description` e `icon`.
            Não há campos opcionais.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos (nome, descrição ou ícone ausentes)")
    })
    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@RequestBody CreateResourceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createResourceUseCase.execute(request));
    }

    @Operation(description = "Retorna todos os recursos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ResourceResponse>> listResources() {
        return ResponseEntity.ok(getResourceUseCase.findAll());
    }

    @Operation(description = "Retorna os dados de um recurso pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recurso encontrado"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResource(
            @Parameter(description = "ID do recurso") @PathVariable UUID id) {
        return ResponseEntity.ok(getResourceUseCase.findById(id));
    }

    @Operation(description = """
            Atualiza nome, descrição e ícone do recurso.
            A atualização substitui todos os campos: `name`, `description` e `icon` são obrigatórios.
            Não há campos opcionais — campos omitidos são tratados como nulos e resultam em 422.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @Parameter(description = "ID do recurso") @PathVariable UUID id,
            @RequestBody UpdateResourceRequest request) {
        return ResponseEntity.ok(updateResourceUseCase.execute(id, request));
    }

    @Operation(
            description = """
                    Altera o status do recurso. Campo obrigatório: `status`. Transições permitidas:
                    - **ACTIVE**: ativa um recurso INACTIVE.
                    - **INACTIVE**: desativa um recurso ACTIVE.

                    Transições inválidas retornam 422 (ex: alterar o status de um recurso já deletado).
                    Para remover use DELETE /resources/{id} (exige INACTIVE).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ResourceResponse> updateResourceStatus(
            @Parameter(description = "ID do recurso") @PathVariable UUID id,
            @RequestBody UpdateResourceStatusRequest request) {
        return ResponseEntity.ok(updateResourceStatusUseCase.execute(id, request.status()));
    }

    @Operation(description = "Remove um recurso (soft delete). O recurso deve estar com status INACTIVE antes de ser deletado; caso contrário retorna 422.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recurso removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "422", description = "Recurso não está inativo")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "ID do recurso") @PathVariable UUID id) {
        deleteResourceUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
