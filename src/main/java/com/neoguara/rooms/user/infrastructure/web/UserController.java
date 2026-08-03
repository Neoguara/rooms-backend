package com.neoguara.rooms.user.infrastructure.web;

import com.neoguara.rooms.user.application.dtos.CreateUserRequest;
import com.neoguara.rooms.user.application.dtos.UpdateUserRequest;
import com.neoguara.rooms.user.application.dtos.UpdateUserStatusRequest;
import com.neoguara.rooms.user.application.dtos.UserResponse;
import com.neoguara.rooms.user.application.usecases.CreateUserUseCase;
import com.neoguara.rooms.user.application.usecases.DeleteUserUseCase;
import com.neoguara.rooms.user.application.usecases.GetUserUseCase;
import com.neoguara.rooms.user.application.usecases.UpdateUserStatusUseCase;
import com.neoguara.rooms.user.application.usecases.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Users", description = "Gerenciamento de usuários")
@RestController
@RequestMapping("/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          UpdateUserStatusUseCase updateUserStatusUseCase,
                          DeleteUserUseCase deleteUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updateUserStatusUseCase = updateUserStatusUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @Operation(description = """
            Cadastra um novo usuário com status inicial ACTIVE.
            Campos obrigatórios: `name`, `email`, `password` (mínimo 6 caracteres) e `role`.
            Não há campos opcionais.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUserUseCase.execute(request));
    }

    @Operation(description = "Retorna todos os usuários cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(getUserUseCase.findAll());
    }

    @Operation(description = "Retorna os dados de um usuário pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        return ResponseEntity.ok(getUserUseCase.findById(id));
    }

    @Operation(description = """
            Atualiza nome, email, senha e papel do usuário.
            Campos obrigatórios: `name` e `email`.
            Campos opcionais: `password` e `role` — quando omitidos, os valores atuais são mantidos.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(updateUserUseCase.execute(id, request));
    }

    @Operation(description = "Remove um usuário (soft delete). O status passa para DELETED e o usuário deixa de ser acessível.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        deleteUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            description = """
                    Altera o status do usuário. Campo obrigatório: `status`. Transições permitidas:
                    - **ACTIVE**: ativa um usuário INACTIVE.
                    - **INACTIVE**: desativa um usuário ACTIVE.

                    Usuários com status DELETED não podem ser modificados.
                    Para remover permanentemente um usuário use DELETE /users/{id}.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(updateUserStatusUseCase.execute(id, request.status()));
    }
}
