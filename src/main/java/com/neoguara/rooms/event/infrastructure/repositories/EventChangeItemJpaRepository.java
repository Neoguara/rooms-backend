package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.domain.entities.EventChangeItem;
import com.neoguara.rooms.event.domain.valueobjects.EventChangeItemId;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface EventChangeItemJpaRepository extends JpaRepository<EventChangeItem, EventChangeItemId> {

    List<EventChangeItem> findByEventRequestIdOrderByPositionAsc(EventRequestId eventRequestId);

    @Query("SELECT i FROM EventChangeItem i WHERE i.eventRequestId.id IN :ids ORDER BY i.position ASC")
    List<EventChangeItem> findByEventRequestIdIn(@Param("ids") Collection<UUID> ids);

    @Query("SELECT COUNT(i) > 0 FROM EventChangeItem i WHERE i.reversalOf.id = :id")
    boolean existsByReversalOf(@Param("id") UUID id);
}
