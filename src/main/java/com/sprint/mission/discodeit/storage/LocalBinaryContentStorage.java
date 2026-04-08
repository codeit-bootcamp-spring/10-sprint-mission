package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.InternalDiscodeitException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
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
            throw new InternalDiscodeitException(
                    "스토리지 디렉터리 생성에 실패했습니다. path: " + root,
                    Map.of("path", root.toString()),
                    e
            );
        }
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        Path path = resolvePath(id);

        try {
            Files.write(path, bytes);
            return id;
        } catch (IOException e) {
            throw new InternalDiscodeitException(
                    "바이너리 저장에 실패했습니다. binaryContentId: " + id,
                    Map.of("binaryContentId", id, "path", path.toString()),
                    e
            );
        }
    }

    @Override
    public InputStream get(UUID id) {
        Path path = resolvePath(id);

        if (!Files.exists(path)) {
            throw new BinaryContentNotFoundException(
                    "바이너리 파일을 찾을 수 없습니다. binaryContentId: " + id,
                    Map.of("binaryContentId", id, "path", path.toString())
            );
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new InternalDiscodeitException(
                    "바이너리 로드에 실패했습니다. binaryContentId: " + id,
                    Map.of("binaryContentId", id, "path", path.toString()),
                    e
            );
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
            throw new InternalDiscodeitException(
                    "다운로드 스트림 처리에 실패했습니다. binaryContentId: " + binaryContentDto.id(),
                    Map.of("binaryContentId", binaryContentDto.id()),
                    e
            );
        }
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
