package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.valueobjects.ApprovalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ApprovalJpaRepository extends JpaRepository<Approval, ApprovalId> {

    @Query("SELECT a FROM Approval a WHERE a.eventRequestId.id = :id ORDER BY a.decidedAt ASC")
    List<Approval> findByEventRequestId(@Param("id") UUID id);
}
