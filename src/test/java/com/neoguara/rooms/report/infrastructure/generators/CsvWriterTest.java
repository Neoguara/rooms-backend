package com.neoguara.rooms.report.infrastructure.generators;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvWriterTest {

    private static final List<String> HEADERS = List.of("Título", "Sala");

    @Test
    void plainFieldsAreWrittenWithoutQuotes() {
        assertEquals("Título;Sala\r\nReunião;Sala 302\r\n",
                write(List.of(List.of("Reunião", "Sala 302"))));
    }

    @Test
    void fieldContainingSeparatorIsQuoted() {
        assertEquals("Título;Sala\r\n\"Reunião; parte 2\";Sala 302\r\n",
                write(List.of(List.of("Reunião; parte 2", "Sala 302"))));
    }

    /** Regra da RFC 4180: aspas dentro do campo aparecem dobradas, e o campo inteiro vai entre aspas. */
    @Test
    void embeddedQuotesAreDoubled() {
        assertEquals("Título;Sala\r\n\"Reunião \"\"urgente\"\"\";Sala 302\r\n",
                write(List.of(List.of("Reunião \"urgente\"", "Sala 302"))));
    }

    /**
     * Sem as aspas, uma quebra de linha no título partiria a linha em duas e desalinharia todas as
     * colunas seguintes do arquivo.
     */
    @Test
    void lineBreakInsideFieldKeepsTheRowTogether() {
        assertEquals("Título;Sala\r\n\"Reunião\nanual\";Sala 302\r\n",
                write(List.of(List.of("Reunião\nanual", "Sala 302"))));
    }

    @Test
    void nullFieldBecomesEmpty() {
        assertEquals("Título;Sala\r\n;Sala 302\r\n",
                write(List.of(Arrays.asList(null, "Sala 302"))));
    }

    @Test
    void separatorIsConfigurable() {
        byte[] csv = CsvWriter.write(HEADERS, List.of(List.of("Reunião", "Sala 302")), ',', false);

        assertEquals("Título,Sala\r\nReunião,Sala 302\r\n", new String(csv, StandardCharsets.UTF_8));
    }

    /** Sem o BOM o Excel lê o arquivo como Latin-1 e os acentos chegam corrompidos ao usuário. */
    @Test
    void bomIsWrittenBeforeTheHeaderWhenRequested() {
        byte[] withBom = CsvWriter.write(HEADERS, List.of(), ';', true);
        byte[] withoutBom = CsvWriter.write(HEADERS, List.of(), ';', false);

        assertTrue(new String(withBom, StandardCharsets.UTF_8).startsWith("﻿"));
        assertFalse(new String(withoutBom, StandardCharsets.UTF_8).startsWith("﻿"));
        assertEquals(withoutBom.length + 3, withBom.length);
    }

    @Test
    void noRowsStillProducesTheHeader() {
        assertEquals("Título;Sala\r\n", write(List.of()));
    }

    private static String write(List<List<String>> rows) {
        return new String(CsvWriter.write(HEADERS, rows, ';', false), StandardCharsets.UTF_8);
    }
}
