package com.neoguara.rooms.report.domain.entities;

import com.neoguara.rooms.report.domain.enums.ReportFormat;
import com.neoguara.rooms.report.domain.enums.ReportStatus;
import com.neoguara.rooms.report.domain.enums.ReportType;
import com.neoguara.rooms.report.domain.validation.ReportValidation;
import com.neoguara.rooms.report.domain.valueobjects.ReportArtifact;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import com.neoguara.rooms.report.domain.valueobjects.ReportParameters;
import com.neoguara.rooms.report.domain.valueobjects.UserId;
import com.neoguara.rooms.shared.domain.exceptions.InvalidStateException;
import com.neoguara.rooms.shared.domain.validation.Notification;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Um relatório pedido por um usuário e produzido fora da requisição HTTP. Nasce {@code PENDING},
 * é retirado da fila por um worker que o move para {@code PROCESSING}, e termina em
 * {@code COMPLETED} ou {@code FAILED}. Não há reprocessamento: pedir de novo é criar outro
 * relatório, o que mantém cada arquivo amarrado às condições em que foi gerado.
 *
 * <p>Apesar da forma parecida, isto <em>não</em> é um {@code EventRequest}: relatório não passa por
 * aprovação, porque só lê a agenda. A semelhança entre os dois é o ciclo de vida, não a regra.
 */
@Entity
@Table(name = "reports")
public class Report {

    /** Tamanho máximo do motivo de falha gravado. Ver {@link #fail(String)}. */
    private static final int MAX_FAILURE_REASON_LENGTH = 500;

    @EmbeddedId
    private ReportId id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "requested_by"))
    private UserId requestedBy;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    private ReportFormat format;

    @Embedded
    private ReportParameters parameters;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    /** Nulo até concluir. É o ponteiro para o arquivo, nunca o arquivo. */
    @Embedded
    private ReportArtifact artifact;

    @Column(length = MAX_FAILURE_REASON_LENGTH)
    private String failureReason;

    private LocalDateTime requestedAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    Report() {}

    private Report(UserId requestedBy, ReportType type, ReportFormat format, ReportParameters parameters) {
        this.id = new ReportId();
        this.requestedBy = requestedBy;
        this.type = type;
        this.format = format;
        this.parameters = parameters;
        this.status = ReportStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public static Report request(
            UserId requestedBy,
            ReportType type,
            ReportFormat format,
            ReportParameters parameters
    ) {
        Report report = new Report(requestedBy, type, format, parameters);
        Notification notification = Notification.create();
        new ReportValidation().validate(report, notification);
        notification.raiseIfHasErrors();
        return report;
    }

    /** Marca o início do processamento. A guarda impede que dois workers assumam o mesmo relatório. */
    public void start() {
        if (this.status != ReportStatus.PENDING)
            throw new InvalidStateException("Only pending reports can start processing");
        this.status = ReportStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(ReportArtifact artifact) {
        requireProcessing("completed");
        if (artifact == null)
            throw new InvalidStateException("A completed report requires an artifact");
        this.artifact = artifact;
        this.status = ReportStatus.COMPLETED;
        this.finishedAt = LocalDateTime.now();
    }

    /**
     * Encerra o relatório em falha. O motivo é truncado em vez de recusado: perder o rastro de uma
     * exceção verbosa é pior do que guardá-la pela metade, e sem isto o relatório ficaria preso em
     * {@code PROCESSING} — com o cliente fazendo polling para sempre.
     */
    public void fail(String reason) {
        requireProcessing("failed");
        this.failureReason = truncate(reason);
        this.status = ReportStatus.FAILED;
        this.finishedAt = LocalDateTime.now();
    }

    /** Descarta o arquivo mantendo o registro de que o relatório existiu e para quem. */
    public void expire() {
        if (this.status != ReportStatus.COMPLETED)
            throw new InvalidStateException("Only completed reports can expire");
        this.status = ReportStatus.EXPIRED;
        this.artifact = null;
    }

    /**
     * Devolve o artefato ou recusa o download. A checagem mora aqui, e não no controller, para que
     * nenhum caminho novo até o arquivo possa esquecê-la.
     */
    public ReportArtifact requireArtifact() {
        if (this.status == ReportStatus.EXPIRED)
            throw new InvalidStateException("This report has expired and is no longer available");
        if (this.status != ReportStatus.COMPLETED)
            throw new InvalidStateException("Only completed reports can be downloaded");
        return artifact;
    }

    public boolean isOwnedBy(UserId candidate) {
        return this.requestedBy.equals(candidate);
    }

    private void requireProcessing(String target) {
        if (this.status != ReportStatus.PROCESSING)
            throw new InvalidStateException("Only processing reports can be marked as " + target);
    }

    private static String truncate(String reason) {
        if (reason == null) return null;
        return reason.length() <= MAX_FAILURE_REASON_LENGTH
                ? reason
                : reason.substring(0, MAX_FAILURE_REASON_LENGTH);
    }

    public ReportId getId() {return id;}
    public UserId getRequestedBy() {return requestedBy;}
    public ReportType getType() {return type;}
    public ReportFormat getFormat() {return format;}
    public ReportParameters getParameters() {return parameters;}
    public ReportStatus getStatus() {return status;}
    public ReportArtifact getArtifact() {return artifact;}
    public String getFailureReason() {return failureReason;}
    public LocalDateTime getRequestedAt() {return requestedAt;}
    public LocalDateTime getStartedAt() {return startedAt;}
    public LocalDateTime getFinishedAt() {return finishedAt;}
}
