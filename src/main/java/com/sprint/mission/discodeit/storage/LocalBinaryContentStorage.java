package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path:./storage}") String rootPath
    ) {
        this.root = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to init storage root dir: " + root, e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try {
            Path path = resolvePath(id);
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store binary content: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            Path path = resolvePath(id);
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read binary content: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        InputStream in = get(dto.id());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(dto.contentType()));
        headers.setContentDisposition(ContentDisposition.attachment().filename(dto.fileName()).build());
        if (dto.size() != null) {
            headers.setContentLength(dto.size());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
