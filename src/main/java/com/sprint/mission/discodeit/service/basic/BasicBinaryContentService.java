package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    BinaryContent binaryContent = binaryContentMapper.toEntity(request);
    BinaryContent saved = binaryContentRepository.save(binaryContent);

    eventPublisher.publishEvent(new BinaryContentCreatedEvent(
        saved.getId(),
        request.getBytes()
    ));

    log.info("[SUCCESS] Created Binary Content Metadata: id={}, fileName={}, size={}",
        saved.getId(), saved.getFileName(), saved.getSize());

    return binaryContentMapper.toDto(saved);
  }

  @Override
  public BinaryContentDto findById(UUID id) {
    return binaryContentMapper.toDto(findBinaryContentById(id));
  }

  @Override
  public BinaryContent findEntityById(UUID id) {
    return findBinaryContentById(id);
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllById(ids).stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public BinaryContentDto updateStatus(UUID id, BinaryContentStatus status) {
    BinaryContent content = findBinaryContentById(id);
    content.updateStatus(status);

    log.info("[SUCCESS] Binary Content Status Updated: id={}, status={}", id, status);

    return binaryContentMapper.toDto(content);
  }

  @Transactional
  @Override
  public void delete(UUID id) {
    BinaryContent content = findBinaryContentById(id);
    binaryContentRepository.delete(content);

    log.info("[SUCCESS] Delete Binary Content: id={}, fileName={}",
        id, content.getFileName());
  }

  // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
  private BinaryContent findBinaryContentById(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new BinaryContentNotFoundException(id));
  }
}
