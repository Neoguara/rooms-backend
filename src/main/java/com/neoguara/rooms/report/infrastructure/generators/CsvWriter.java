package com.neoguara.rooms.report.infrastructure.generators;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Escreve CSV conforme a RFC 4180. É código próprio em vez de biblioteca porque o problema inteiro
 * cabe em duas regras — envolver em aspas quando o campo contém separador, aspas ou quebra de
 * linha, e dobrar as aspas internas — e o {@code ~/.m2} deste ambiente não tem nenhuma lib de CSV.
 */
public final class CsvWriter {

    /** Sem isto o Excel lê o arquivo como Latin-1 e come todos os acentos. */
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    /** A RFC especifica CRLF, e é o que o Excel espera. */
    private static final String LINE_BREAK = "\r\n";

    private CsvWriter() {}

    public static byte[] write(List<String> headers, List<List<String>> rows, char separator, boolean withBom) {
        StringBuilder out = new StringBuilder();
        appendLine(out, headers, separator);
        for (List<String> row : rows) appendLine(out, row, separator);

        byte[] body = out.toString().getBytes(StandardCharsets.UTF_8);
        if (!withBom) return body;

        byte[] withPreamble = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, withPreamble, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, withPreamble, UTF8_BOM.length, body.length);
        return withPreamble;
    }

    private static void appendLine(StringBuilder out, List<String> values, char separator) {
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) out.append(separator);
            out.append(escape(values.get(i), separator));
        }
        out.append(LINE_BREAK);
    }

    private static String escape(String value, char separator) {
        if (value == null || value.isEmpty()) return "";

        boolean mustQuote = value.indexOf(separator) >= 0
                || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0
                || value.indexOf('\r') >= 0;

        if (!mustQuote) return value;
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
