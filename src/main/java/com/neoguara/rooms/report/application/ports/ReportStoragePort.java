package com.neoguara.rooms.report.application.ports;

import com.neoguara.rooms.report.domain.valueobjects.ReportId;

import java.io.InputStream;

/**
 * Onde os bytes do relatório ficam. A porta fala em {@code InputStream} e não em {@code Resource}
 * do Spring para que trocar disco por S3 seja um adapter novo, sem que a camada de aplicação saiba
 * da diferença.
 */
public interface ReportStoragePort {

    /** Grava o conteúdo e devolve a chave que permite recuperá-lo depois. */
    String store(ReportId id, String filename, byte[] content);

    /** Abre o conteúdo para leitura. Quem chama é responsável por fechar o stream. */
    InputStream load(String storageKey);

    void delete(String storageKey);
}
