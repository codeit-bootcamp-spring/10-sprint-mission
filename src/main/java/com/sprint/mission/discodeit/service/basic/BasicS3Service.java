package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.events.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class BasicS3Service {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService;
  private final BinaryContentUploadService binaryContentUploadService;

  public UUID upload(MultipartFile file) throws IOException {
    Objects.requireNonNull(file, "파일이 유효하지 않습니다.");

    BinaryContent binaryContent = binaryContentService.create(
        new BinaryContentCreateRequestDTO(
            file.getContentType(),
            file.getBytes()
        )
    );
    binaryContentUploadService.upload(binaryContent.getId(), file.getBytes());
    return binaryContent.getId();
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

    return binaryContentStorage.download(binaryContentDto);
  }

  private UUID extractUuid(String key) {
    int dotIndex = key.indexOf('.');
    String uuidPart = dotIndex >= 0 ? key.substring(0, dotIndex) : key;
    return UUID.fromString(uuidPart);
  }
}
