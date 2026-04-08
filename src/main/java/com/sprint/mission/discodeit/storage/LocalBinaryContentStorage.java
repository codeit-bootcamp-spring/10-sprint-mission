package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
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
            throw new RuntimeException("스토리지 폴더를 초기화할 수 없습니다.", e);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        try {
            Files.write(resolvePath(id), bytes);
            return id;
        } catch (IOException e) {
            throw new RuntimeException("파일을 저장할 수 없습니다. id: " + id, e);
        }
    }

    @Override
    public InputStream get(UUID id) {
        try {
            return Files.newInputStream(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽을 수 없습니다. id: " + id, e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        try {
            InputStream inputStream = get(dto.id());

            Resource resource = new InputStreamResource(inputStream);

            // 5. BinaryContentDto와 바이너리 데이터를 활용해 ResponseEntity<Resource> 응답 생성
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(dto.contentType()))
                    // Content-Length를 지정하여 클라이언트가 다운로드 진행률을 알 수 있도록 함
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(dto.size()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dto.fileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("파일을 다운로드할 수 없습니다. id: " + dto.id(), e);
        }
    }

    private Path resolvePath(UUID id) {
        return this.root.resolve(id.toString());
    }

}
