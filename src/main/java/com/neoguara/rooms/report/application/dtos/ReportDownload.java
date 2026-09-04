package com.neoguara.rooms.report.application.dtos;

import java.io.InputStream;

/**
 * O arquivo pronto para ser escrito na resposta. Carrega o stream, e não os bytes, para que baixar
 * um relatório grande não signifique carregá-lo inteiro na memória do servidor.
 */
public record ReportDownload(InputStream content, String filename, String contentType, long sizeBytes) {
}
