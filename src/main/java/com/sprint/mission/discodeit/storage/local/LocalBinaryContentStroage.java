package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStroage implements BinaryContentStorage {
    private Path root;

    public LocalBinaryContentStroage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    public LocalBinaryContentStroage(Path root) {

    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("스토리지 생성 실패", e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resovePath(id);

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return id;
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resovePath(id);

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("파일 조회 실패", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        InputStream inputStream = get(binaryContentDto.id());

        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .body(resource);
    }

    public Path resovePath(UUID id) {
        return root.resolve(id.toString());
    }
}
