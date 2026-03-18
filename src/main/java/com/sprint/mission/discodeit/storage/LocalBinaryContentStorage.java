package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
            @Value("${discodeit.storage.local.root-path}") Path root
    ) {
        this.root = root;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR,
                    "스토리지 디렉터리 생성에 실패했습니다. path: " + root);
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);

        try {
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR,
                    "바이너리 저장에 실패했습니다. binaryContentId: " + id);
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);

        if (!Files.exists(path)) {
            throw new ApiException(ErrorCode.BINARY_CONTENT_NOT_FOUND,
                    "바이너리 파일을 찾을 수 없습니다. binaryContentId: " + id);
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR,
                    "바이너리 로드에 실패했습니다. binaryContentId: " + id);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
        try (InputStream inputStream = get(binaryContentDto.id())) {
            byte[] bytes = inputStream.readAllBytes();
            Resource resource = new ByteArrayResource(bytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(binaryContentDto.contentType()));
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(binaryContentDto.fileName())
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(binaryContentDto.size())
                    .body(resource);
        } catch (IOException e) {
            throw new ApiException(ErrorCode.INTERNAL_ERROR,
                    "다운로드 스트림 처리에 실패했습니다. binaryContentId: " + binaryContentDto.id());
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
