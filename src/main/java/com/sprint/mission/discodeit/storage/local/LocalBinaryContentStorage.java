package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
        this.root = Paths.get(rootPath);
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("로컬 저장소 디렉토리를 초기화할 수 없습니다.", e);
        }
    }

    @Override
    public UUID put(UUID uuid, byte[] bytes) {
        try {
            Files.write(resolvePath(uuid), bytes);
            return uuid;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public InputStream get(UUID uuid) {
        try {
            return Files.newInputStream(resolvePath(uuid));
        } catch (IOException e) {
            throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다.", e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        InputStream inputStream = get(dto.getId());
        Resource resource = new InputStreamResource(inputStream);


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(dto.getContentType()))
                .contentLength(dto.getSize())
                .body(resource);
    }

    private Path resolvePath(UUID uuid) {
        return root.resolve(uuid.toString());
    }
}