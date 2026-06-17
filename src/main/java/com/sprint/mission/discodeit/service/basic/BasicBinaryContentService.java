package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public BinaryContent create(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new FileEmptyException(Map.of(
          "parameterName", "file",
          "reason", "전송된 파일이 없거나 내용이 비어있습니다."));
    }

    try {
      BinaryContent binaryContent = new BinaryContent(
          file.getOriginalFilename(),
          file.getSize(),
          file.getContentType()
      );
      binaryContentRepository.save(binaryContent); // 메타데이터 저장

      // 메타데이터 저장 이벤트 발행
      eventPublisher.publishEvent(
          new BinaryContentCreatedEvent(binaryContent.getId(), file.getBytes()));
      return binaryContent;

    } catch (IOException e) {
      throw new FileUploadException(Map.of(
          "fileName", Objects.requireNonNullElse(file.getOriginalFilename(), "unknown"),
          "fileSize", file.getSize(),
          "contentType", Objects.requireNonNullElse(file.getContentType(), "unknown")
      ), e);
    }
  }

  @Override
  public BinaryContent findById(UUID id) {
    return getOrThrowBinaryContent(id);
  }

  @Override
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids);
  }

  @Override
  @Transactional
  public BinaryContent updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = getOrThrowBinaryContent(binaryContentId);

    // 업로드 상태값 업데이트
    binaryContent.updateStatus(status);

    return binaryContent;
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    BinaryContent content = getOrThrowBinaryContent(id);
    binaryContentRepository.delete(content);
  }

  // --- Helper Methods ---

  // 바이너리 컨텐츠 검증
  private BinaryContent getOrThrowBinaryContent(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(
            () -> new BinaryContentNotFoundException(Map.of("requestedBinaryContentId", id)));
  }
}
