package com.neoguara.rooms.room.infrastructure.web.room;

import com.neoguara.rooms.room.application.dtos.room.CreateRoomRequest;
import com.neoguara.rooms.room.application.dtos.room.RoomResponse;
import com.neoguara.rooms.room.application.dtos.room.UpdateRoomRequest;
import com.neoguara.rooms.room.application.usecases.room.CreateRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.DeleteRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.GetRoomUseCase;
import com.neoguara.rooms.room.application.usecases.room.UpdateRoomUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final CreateRoomUseCase createRoomUseCase;
    private final GetRoomUseCase getRoomUseCase;
    private final UpdateRoomUseCase updateRoomUseCase;
    private final DeleteRoomUseCase deleteRoomUseCase;

    public RoomController(CreateRoomUseCase createRoomUseCase,
                          GetRoomUseCase getRoomUseCase,
                          UpdateRoomUseCase updateRoomUseCase,
                          DeleteRoomUseCase deleteRoomUseCase) {
        this.createRoomUseCase = createRoomUseCase;
        this.getRoomUseCase = getRoomUseCase;
        this.updateRoomUseCase = updateRoomUseCase;
        this.deleteRoomUseCase = deleteRoomUseCase;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> create(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoomUseCase.execute(request));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> findAll() {
        return ResponseEntity.ok(getRoomUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(getRoomUseCase.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(@PathVariable UUID id, @RequestBody UpdateRoomRequest request) {
        return ResponseEntity.ok(updateRoomUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        deleteRoomUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
