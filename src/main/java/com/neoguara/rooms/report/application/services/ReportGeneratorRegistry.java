package com.neoguara.rooms.report.application.services;

import com.neoguara.rooms.report.application.ports.ReportGenerator;
import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Encontra o gerador de um par {@code (tipo, formato)} entre os que o Spring registrou. Existe para
 * que a combinação seja checada na submissão, e não só quando o worker acorda: descobrir que o
 * formato não existe depois de responder 202 significaria um relatório {@code FAILED} no lugar de
 * um erro imediato.
 */
@Service
public class ReportGeneratorRegistry {

    private final List<ReportGenerator> generators;

    public ReportGeneratorRegistry(List<ReportGenerator> generators) {
        this.generators = generators;
    }

    public ReportGenerator require(ReportType type, ReportFormat format) {
        return generators.stream()
                .filter(generator -> generator.supports(type, format))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Report generator", type + "/" + format));
    }
}
