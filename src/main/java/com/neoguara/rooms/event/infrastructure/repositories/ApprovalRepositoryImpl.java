package com.neoguara.rooms.event.infrastructure.repositories;

import com.neoguara.rooms.event.application.ports.ApprovalRepositoryPort;
import com.neoguara.rooms.event.domain.entities.Approval;
import com.neoguara.rooms.event.domain.valueobjects.EventRequestId;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApprovalRepositoryImpl implements ApprovalRepositoryPort {

    private final ApprovalJpaRepository jpaRepository;

    public ApprovalRepositoryImpl(ApprovalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Approval save(Approval approval) {
        return jpaRepository.save(approval);
    }

    @Override
    public List<Approval> findByEventRequestId(EventRequestId eventRequestId) {
        return jpaRepository.findByEventRequestId(eventRequestId.id());
    }
}
