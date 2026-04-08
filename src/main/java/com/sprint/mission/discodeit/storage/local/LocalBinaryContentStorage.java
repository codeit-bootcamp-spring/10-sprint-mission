package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentReadException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentSaveException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentStorageInitException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
@Component
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") Path root
    ) {
        this.root = root;
    }

    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                log.error("[BINARY_CONTENT_STORAGE_INIT] 파일 저장소 초기화 실패: root={}", root, e);
                throw new BinaryContentStorageInitException(root, e);
            }
        }
        log.info("[BINARY_CONTENT_STORAGE_INIT] 파일 저장소 준비 완료: root={}", root);
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path filePath = resolvePath(binaryContentId);
        if (Files.exists(filePath)) {
            log.warn("[BINARY_CONTENT_PUT] 파일 쓰기 실패 - 파일 경로 중복: binaryContentId={}",
                    binaryContentId);
            throw new BinaryContentSaveException(binaryContentId);
        }

        try (OutputStream outputStream = Files.newOutputStream(filePath)) {
            outputStream.write(bytes);
        } catch (IOException e) {
            log.error("[BINARY_CONTENT_PUT] 파일 쓰기 실패: binaryContentId={}",
                    binaryContentId, e);
            throw new BinaryContentSaveException(binaryContentId, e);
        }
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path filePath = resolvePath(binaryContentId);
        if (Files.notExists(filePath)) {
            log.error("[BINARY_CONTENT_GET] 파일 읽기 실패 - 파일이 존재하지 않음: binaryContentId={}",
                    binaryContentId);
            throw new BinaryContentReadException(binaryContentId);
        }
        try {
            return Files.newInputStream(filePath);
        } catch (IOException e) {
            log.error("[BINARY_CONTENT_GET] 파일 읽기 실패: binaryContentId={}",
                    binaryContentId, e);
            throw new BinaryContentReadException(binaryContentId, e);
        }
    }

    private Path resolvePath(UUID key) {
        return root.resolve(key.toString());
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto metaData) {
        log.debug("[BINARY_CONTENT_DOWNLOAD] 파일 다운로드 시작: binaryContentId={}",
                metaData.id());

        InputStream inputStream = get(metaData.id());
        Resource resource = new InputStreamResource(inputStream);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metaData.fileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, metaData.contentType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(metaData.size()))
                .body(resource);
    }
}
