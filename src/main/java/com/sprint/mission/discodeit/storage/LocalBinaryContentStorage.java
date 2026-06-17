package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path rootPath) {
        this.root = rootPath;
    }

    // 초기화
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed ot initialize storage directory", e);
        }
    }

    // 첨부 파일 저장
    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            // 성능 향상을 위한 지연 (3초)
            Thread.sleep(3000);

            Path path = resolvePath(binaryContentId);
            Files.write(path, bytes);

            return binaryContentId;
        } catch (InterruptedException e) {                      // 스레드 오류
            // 대기 중 오류 발생 시, 해당 스레드의 인터럽트 상태 복구
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while simulating delay", e);
        } catch (IOException e) {                               // 파일 쓰기 오류
            throw new RuntimeException("Failed to store binary content", e);
        }
    }

    // 첨부 파일 -> InputStream으로 변환
    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            return Files.newInputStream(resolvePath(binaryContentId));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read binary content file", e);
        }
    }

    // 첨부 파일 다운로드
    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        Resource resource = new InputStreamResource(get(binaryContentDto.id()));

        log.info("[BINARY_CONTENT_DOWNLOAD] 첨부파일 다운로드 완료: id={}, filename={}",
                binaryContentDto.id(),
                binaryContentDto.fileName()
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + binaryContentDto.fileName() + "\"")
                .contentType(MediaType.parseMediaType(binaryContentDto.contentType()))
                .body(resource);
    }

    private Path resolvePath(UUID binaryContentId) {
        return root.resolve(binaryContentId.toString());
    }
}
