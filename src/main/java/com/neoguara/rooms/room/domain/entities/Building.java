package com.neoguara.rooms.room.domain.entities;

import com.neoguara.rooms.room.domain.enums.BuildingStatus;
import com.neoguara.rooms.room.domain.valueobjects.BuildingId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "buildings")
public class Building {

    @EmbeddedId
    private BuildingId id;

    private String name;
    private String address;
    private Integer totalFloors;
    private BuildingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    Building() {}

    public void activate () {
        if (this.status == BuildingStatus.ARCHIVED) {
            throw new Exception("Archived building cannot be activated");
        }
        this.status = BuildingStatus.ACTIVE;
    }

    public void deactivate () {
        if (status == BuildingStatus.ARCHIVED) {
//            throw new BusinessException("Archived building cannot be deactivated");
        }
        this.status = BuildingStatus.INACTIVE;
    }

    public void archive() {
        this.status = BuildingStatus.ARCHIVED;
    }

    public void restore () {
        if (status != BuildingStatus.ARCHIVED) {

        }
        this.status = BuildingStatus.ACTIVE;
    }

    public boolean isAvailable() {
        return status == BuildingStatus.ACTIVE;
    }



}
