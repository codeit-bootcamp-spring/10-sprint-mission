package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {
    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Path.of(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] content) {
        Path targetPath = resolvePath(id);
        try {
            Files.write(targetPath, content);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            return Files.newInputStream(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDTO dto) {
        // get 메소드를 사용하여 데이터 조회
        InputStream inputStream = get(dto.id());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + dto.fileName() + "\"")
                .body(resource);
    }

    // 4. 경로 규칙 정의: {root}/{UUID}
    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}