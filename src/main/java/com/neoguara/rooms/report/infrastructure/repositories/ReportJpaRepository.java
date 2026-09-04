package com.neoguara.rooms.report.infrastructure.repositories;

import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportJpaRepository extends JpaRepository<Report, ReportId> {
    List<Report> findByStatus(ReportStatus status);
    List<Report> findAllByOrderByRequestedAtDesc();
}
