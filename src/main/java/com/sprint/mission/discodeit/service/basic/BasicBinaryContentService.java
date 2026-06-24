package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.message.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    log.debug(
        "바이너리 컨텐츠 생성 시작: fileName={}, size={}, contentType={}",
        request.fileName(),
        request.bytes().length,
        request.contentType());

    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
    binaryContentRepository.save(binaryContent);
    eventPublisher.publishEvent(
        new BinaryContentCreatedEvent(binaryContent, binaryContent.getCreatedAt(), bytes));

    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.debug("바이너리 컨텐츠 조회 시작: id={}", binaryContentId);
    BinaryContentDto dto =
        binaryContentRepository
            .findById(binaryContentId)
            .map(binaryContentMapper::toDto)
            .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
    return dto;
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("바이너리 컨텐츠 목록 조회 시작: ids={}", binaryContentIds);
    List<BinaryContentDto> dtos =
        binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    return dtos;
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.debug("바이너리 컨텐츠 삭제 시작: id={}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw BinaryContentNotFoundException.withId(binaryContentId);
    }
    binaryContentRepository.deleteById(binaryContentId);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    log.debug("바이너리 콘텐츠 상태 업데이트 시작: id={}, status={}", binaryContentId, status);

    BinaryContent binaryContent =
        binaryContentRepository
            .findById(binaryContentId)
            .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

    BinaryContentDto before = binaryContentMapper.toDto(binaryContent);

    binaryContent.updateStatus(status);
    binaryContentRepository.save(binaryContent);

    BinaryContentDto after = binaryContentMapper.toDto(binaryContent);

    eventPublisher.publishEvent(new BinaryContentUpdatedEvent(before, after, Instant.now()));

    return after;
  }
}
