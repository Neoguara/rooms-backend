package com.neoguara.rooms.report.infrastructure.repositories;

import com.neoguara.rooms.report.application.ports.ReportRepositoryPort;
import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReportRepositoryImpl implements ReportRepositoryPort {

    private final ReportJpaRepository jpaRepository;

    public ReportRepositoryImpl(ReportJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Report save(Report report) {
        return jpaRepository.save(report);
    }

    @Override
    public Optional<Report> findById(ReportId id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Report> findByStatus(ReportStatus status) {
        return jpaRepository.findByStatus(status);
    }

    /** A ordenação vem do nome do método derivado; a porta promete o mais recente primeiro. */
    @Override
    public List<Report> findAll() {
        return jpaRepository.findAllByOrderByRequestedAtDesc();
    }
}
