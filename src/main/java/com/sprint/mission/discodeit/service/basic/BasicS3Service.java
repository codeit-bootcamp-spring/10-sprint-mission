package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class BasicS3Service {

    private final S3BinaryContentStorage s3BinaryContentStorage;

    public UUID upload(MultipartFile file) throws IOException {
        Objects.requireNonNull(file, "file must not be null");
        UUID key = UUID.randomUUID();
        s3BinaryContentStorage.put(key, file);
        return key;
    }

    public ResponseEntity<?> download(String key, String filename) {
        Objects.requireNonNull(key, "key must not be null");

        BinaryContentDto binaryContentDto = new BinaryContentDto(
            extractUuid(key),
            (filename != null && !filename.isBlank()) ? filename : key,
            0L,
            null,
            BinaryContentStatus.SUCCESS,
            null
        );

        return s3BinaryContentStorage.download(key, binaryContentDto);
    }

    private UUID extractUuid(String key) {
        int dotIndex = key.indexOf('.');
        String uuidPart = dotIndex >= 0 ? key.substring(0, dotIndex) : key;
        return UUID.fromString(uuidPart);
    }
}
