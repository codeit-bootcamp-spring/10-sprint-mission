package com.sprint.mission.discodeit.storage;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") String rootPath
    ) {
        this.root = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize storage root: " + root, e);
        }
    }

    @Override
    public UUID put(UUID uuid, byte[] bytes) {
        Path path = resolvePath(uuid);
        try {
            Files.write(path, bytes, CREATE, TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return uuid;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write binary content: " + uuid, e);
        }
    }

    @Override
    public InputStream get(UUID uuid) {
        Path path = resolvePath(uuid);
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read binary content: " + uuid, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        byte[] bytes = binaryContentDto.bytes();
        if (bytes == null || bytes.length == 0) {
            try (InputStream in = get(binaryContentDto.id())) {
                bytes = in.readAllBytes();
            } catch (IOException e) {
                throw new IllegalStateException(
                    "Failed to build download response for: " + binaryContentDto.id(), e);
            }
        }

        Resource resource = new ByteArrayResource(bytes);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (binaryContentDto.contentType() != null && !binaryContentDto.contentType().isBlank()) {
            mediaType = MediaType.parseMediaType(binaryContentDto.contentType());
        }

        String fileName =
            binaryContentDto.fileName() != null && !binaryContentDto.fileName().isBlank()
                ? binaryContentDto.fileName()
                : binaryContentDto.id().toString();

        return ResponseEntity.ok()
            .contentType(mediaType)
            .contentLength(bytes.length)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment().filename(fileName).build().toString()
            )
            .body(resource);
    }

    Path resolvePath(UUID uuid) {
        return root.resolve(uuid.toString());
    }
}
