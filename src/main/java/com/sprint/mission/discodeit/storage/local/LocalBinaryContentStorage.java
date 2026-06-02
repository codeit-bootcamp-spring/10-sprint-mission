package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.etc.FileProcessingException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String path) {
        this.root = Path.of(path);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(this.root);
            log.info("[Storage Init] Success! Root Path: {}", root.toAbsolutePath());
        } catch (IOException e) {
            log.error("[Storage Init] Failed! Path: {}", root, e);
            throw FileProcessingException.storageInitFailed(root.toString(), e);
        }
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path path = resolvePath(binaryContentId);

        try {
            Files.write(path, bytes);
            log.info("[File Saved] ID: {}, Path: {}, Size: {} bytes",
                    binaryContentId, path.getFileName(), bytes.length);
            return binaryContentId;
        } catch (IOException e) {
            log.error("[File Save Failed] ID: {}, Error: {}", binaryContentId, e.getMessage());
            throw FileProcessingException.writeFailed(binaryContentId, e);
        }
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);

        if (Files.notExists(path)) {
            log.error("[File Read Failed] Not Found on Disk. ID: {}, Path: {}",
                    binaryContentId, path.toAbsolutePath());
            throw FileProcessingException.fileMissingOnDisk(binaryContentId);
        }

        try {
            InputStream inputStream = new BufferedInputStream(Files.newInputStream(path));
            log.debug("[File Stream Opened] ID: {}, Path: {}", binaryContentId, path.getFileName());
            return inputStream;
        } catch (IOException e) {
            log.error("[File Read Error] ID: {}, Error: {}", binaryContentId, e.getMessage());
            throw FileProcessingException.readFailed(binaryContentId.toString(), e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto.Response response) {
        InputStream inputStream = get(response.id());
        Resource resource = new InputStreamResource(inputStream);

        String contentDisposition = ContentDisposition.attachment()
                .filename(response.fileName(), StandardCharsets.UTF_8)
                .build()
                .toString();

        log.info("[File Downloaded] ID: {}, Name: {}, ContentType: {}",
                response.id(), response.fileName(), response.contentType());

        return ResponseEntity.ok()
                .contentType(response.contentType() != null ?
                        MediaType.parseMediaType(response.contentType()) :
                        MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(response.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    public void delete(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);
        try {
            if (Files.deleteIfExists(path)) {
                log.info("[File Deleted] ID: {}, Path: {}", binaryContentId, path.getFileName());
            } else {
                log.warn("[File Delete Skipped] Not found on Disk. ID: {}", binaryContentId);
            }
        } catch (IOException e) {
            log.error("[File Delete Error] ID: {}, Error: {}", binaryContentId, e.getMessage());
            throw FileProcessingException.deleteFailed(binaryContentId, e);
        }
    }

    private Path resolvePath(UUID binaryContentId) {
        return root.resolve("LocalFile_" + binaryContentId.toString());
    }
}
