package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private Path root;


    public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path path) {
        this.root = path;
    }


    @PostConstruct
    public void init() {
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new BusinessLogicException(ExceptionCode.STORAGE_PATH_INIT_EXCEPTION);
            }
        }
    }

    public Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }


    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path fullPath = resolvePath(id); // todo: 확장자는?
        try {
            Files.write(fullPath, bytes);
            return id;
        } catch (IOException e) {
            throw new BusinessLogicException(ExceptionCode.ATTACHMENT_SAVE_EXCEPTION);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path fullPath = resolvePath(id);

        if (Files.exists(fullPath)) {
            try {
                return Files.newInputStream(fullPath);
            } catch (IOException e) {
                throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_LOAD_EXCEPTION);
            }
        } else {
            throw new BusinessLogicException(ExceptionCode.BINARY_CONTENT_NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
        InputStream inputStream = get(binaryContentDto.getId());
        Resource resource = new InputStreamResource(inputStream);

        String headerContent = "attachment; filename=\"" + binaryContentDto.getFileName() + "\"";

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_DISPOSITION, headerContent)
            .body(resource);
    }
}
