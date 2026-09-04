package com.neoguara.rooms.report.domain.validation;

import com.neoguara.rooms.report.domain.entities.Report;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.shared.domain.validation.Notification;
import com.neoguara.rooms.shared.domain.validation.Validator;

public class ReportValidation implements Validator<Report> {

    @Override
    public void validate(Report target, Notification notification) {
        notification
                .addErrorIf(target.getRequestedBy() == null, "requestedBy is required")
                .addErrorIf(target.getType() == null, "type is required")
                .addErrorIf(target.getFormat() == null, "format is required")
                .addErrorIf(target.getParameters() == null, "parameters are required");

        ReportParameters parameters = target.getParameters();
        if (parameters == null) return;

        notification
                .addErrorIf(parameters.startAt() == null, "startAt is required")
                .addErrorIf(parameters.endAt() == null, "endAt is required");

        // A comparação só faz sentido com as duas pontas presentes; sem esta saída, um intervalo
        // sem início acusaria "endAt must be after startAt" além do erro que o cliente já viu.
        if (parameters.startAt() == null || parameters.endAt() == null) return;

        notification.addErrorIf(
                !parameters.endAt().isAfter(parameters.startAt()),
                "endAt must be after startAt");
    }
}
