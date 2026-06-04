package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
import com.sprint.mission.discodeit.events.AdminBinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.events.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.FieldNotValidException;
import com.sprint.mission.discodeit.exception.RequestNullException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final BinaryContentStorage binaryContentStorage;

  // 바이너리 컨텐츠 생성 시도 및 Retry 정책 명시
  @Retryable(
      retryFor = {IOException.class, IllegalStateException.class, SdkException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  @Transactional
  @Override
  public BinaryContent create(BinaryContentCreateRequestDTO req) {
    if (req == null) {
      throw new RequestNullException();
    }
    if (req.file() == null) {
      throw new FieldNotValidException("file");
    }
    if (req.contentType() == null || req.contentType().isBlank()) {
      throw new FieldNotValidException("contentType");
    }

    BinaryContent binaryContent = binaryContentRepository.save(new BinaryContent(
        "binary-content",
        (long) req.file().length,
        req.contentType()
    ));

    eventPublisher.publishEvent(
        new BinaryContentCreatedEvent(binaryContent.getId(), req.file())
    );

    return binaryContent;
  }

  @Recover
  public void recover(Exception e, UUID binaryContentId) {
    AdminBinaryContentUploadFailedEvent event = new AdminBinaryContentUploadFailedEvent(
        Thread.currentThread().getId(),
        binaryContentId,
        e.getMessage()
    );

    eventPublisher.publishEvent(event);
  }

  @Transactional
  @Override
  public BinaryContentDto find(UUID id) {
    // id null 체크
    if (id == null) {
      throw new FieldNotValidException("id");
    }

    // Id로 BinaryContent 조회 메서드 시작 로그
    log.trace("[BinaryContent] BinaryContent 조회 메서드 시작: id={}", id);

    // BinaryContent 레포지토리에서 BinaryContent 조회
    BinaryContent binaryContent = getBinaryContent(id);

    log.debug("[BinaryContent] 조회된 BinaryContent 정보: id={}, fileName={}, size={}",
        binaryContent.getId(), binaryContent.getFileName(), binaryContent.getSize());

    log.info("[BinaryContent] BinaryContent 조회 성공");
    return binaryContentMapper.toDto(binaryContent);
  }

  @Transactional
  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    // id 리스트 null 체킹
    if (ids == null) {
      throw new FieldNotValidException("ids");
    }

    log.trace("[BinaryContent] ID 리스트로 BinaryContent 조회 메서드 시작: ids={}", ids);

    // 첨부 파일 ID 리스트 기반으로 첨부 파일 DTO 생성
    List<BinaryContentDto> result = binaryContentRepository.findAllByIdIn(ids).stream()
        .map(this::toDtoWithBytes)
        .toList();

    // 첫 번째 첨부파일의 Id만 로그로 찍음.
    log.info("[BinaryContent] 조회 성공: binaryContentId={}",
        result
            .stream()
            .filter(Objects::nonNull)
            .findFirst()
            .get().id());

    return result;
  }

  @Transactional
  @Override
  public void delete(UUID id) {
    if (id == null) {
      throw new FieldNotValidException("id");
    }

    log.trace("[BinaryContent] 첨부 파일 삭제 메서드 시작: id={}", id);

    BinaryContent binaryContent = getBinaryContent(id);

    log.info("[BinaryContent] 삭제 될 첨부 파일 정보: id={}", binaryContent.getId());

    binaryContentRepository.deleteById(id);

    log.info("[BinaryContent] 첨부 파일 삭제 성공: id={}", id);
  }

  // 기존의 트랜잭션이 있든 없든 새로운 트랜잭션으로 실행한다.
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(
        () -> new BinaryContentNotFoundException(binaryContentId)
    );
    binaryContent.updateStatus(status);

    return toDtoWithBytes(binaryContent);
  }

  private BinaryContentDto toDtoWithBytes(BinaryContent binaryContent) {
    byte[] bytes = new byte[0];
    if (binaryContent.getStatus() == BinaryContentStatus.SUCCESS) {
      try (InputStream in = binaryContentStorage.get(binaryContent.getId())) {
        bytes = in.readAllBytes();
      } catch (IOException | IllegalStateException e) {
        log.warn("[BinaryContent] binary data read failed: id={}", binaryContent.getId(), e);
      }
    }
    return new BinaryContentDto(
        binaryContent.getId(),
        binaryContent.getFileName(),
        binaryContent.getSize(),
        binaryContent.getContentType(),
        binaryContent.getStatus(),
        bytes
    );
  }

  public BinaryContent getBinaryContent(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new BinaryContentNotFoundException(id));
  }

}
