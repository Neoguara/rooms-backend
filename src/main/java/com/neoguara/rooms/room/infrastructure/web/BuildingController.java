package com.neoguara.rooms.room.infrastructure.web;

import com.neoguara.rooms.room.application.dtos.building.BuildingResponse;
import com.neoguara.rooms.room.application.dtos.building.CreateBuildingRequest;
import com.neoguara.rooms.room.application.dtos.building.UpdateBuildingRequest;
import com.neoguara.rooms.room.application.usecases.building.CreateBuildingUseCase;
import com.neoguara.rooms.room.application.usecases.building.DeleteBuildingUseCase;
import com.neoguara.rooms.room.application.usecases.building.GetBuildingUseCase;
import com.neoguara.rooms.room.application.usecases.building.UpdateBuildingUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/buildings")
public class BuildingController {

    private final CreateBuildingUseCase createBuildingUseCase;
    private final GetBuildingUseCase getBuildingUseCase;
    private final UpdateBuildingUseCase updateBuildingUseCase;
    private final DeleteBuildingUseCase deleteBuildingUseCase;

    public BuildingController(CreateBuildingUseCase createBuildingUseCase,
                              GetBuildingUseCase getBuildingUseCase,
                              UpdateBuildingUseCase updateBuildingUseCase,
                              DeleteBuildingUseCase deleteBuildingUseCase) {
        this.createBuildingUseCase = createBuildingUseCase;
        this.getBuildingUseCase = getBuildingUseCase;
        this.updateBuildingUseCase = updateBuildingUseCase;
        this.deleteBuildingUseCase = deleteBuildingUseCase;
    }

    @PostMapping
    public ResponseEntity<BuildingResponse> create(@RequestBody CreateBuildingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createBuildingUseCase.execute(request));
    }

    @GetMapping
    public ResponseEntity<List<BuildingResponse>> findAll() {
        return ResponseEntity.ok(getBuildingUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(getBuildingUseCase.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponse> updateById(@PathVariable UUID id, @RequestBody UpdateBuildingRequest request) {
        return ResponseEntity.ok(updateBuildingUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        deleteBuildingUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
