package com.neoguara.rooms.report.infrastructure.adapters;

import com.neoguara.rooms.report.application.ports.ReportStoragePort;
import com.neoguara.rooms.report.domain.valueobjects.ReportId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Guarda os arquivos em disco, num diretório configurável. O banco fica só com a chave — em H2 em
 * arquivo, disputado por lock com a aplicação, uma coluna BLOB seria o pior lugar para os bytes.
 */
@Component
public class FileSystemReportStorageAdapter implements ReportStoragePort {

    private final Path root;

    FileSystemReportStorageAdapter(@Value("${report.storage.path:./data/reports}") String path) {
        this.root = Paths.get(path).toAbsolutePath().normalize();
    }

    /**
     * A chave é o id do relatório, não o nome exibido. O nome vem do tipo e da data e repetiria
     * entre relatórios; o id não repete, então dois pedidos iguais nunca sobrescrevem um ao outro.
     */
    @Override
    public String store(ReportId id, String filename, byte[] content) {
        String storageKey = id.id() + extensionOf(filename);
        try {
            Files.createDirectories(root);
            Files.write(resolve(storageKey), content);
            return storageKey;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not store report " + id.id(), e);
        }
    }

    @Override
    public InputStream load(String storageKey) {
        try {
            return Files.newInputStream(resolve(storageKey));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read report file " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not delete report file " + storageKey, e);
        }
    }

    /**
     * As chaves são geradas a partir de UUID e nunca vêm do cliente, mas a conferência custa uma
     * linha: se alguma vez passarem a vir, um {@code ../} não deve alcançar fora do diretório.
     */
    private Path resolve(String storageKey) {
        Path resolved = root.resolve(storageKey).normalize();
        if (!resolved.startsWith(root))
            throw new IllegalArgumentException("Storage key escapes the report directory: " + storageKey);
        return resolved;
    }

    private static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot);
    }
}
