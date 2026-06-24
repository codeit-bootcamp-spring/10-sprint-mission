package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.repository.JPABinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.common.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.sse.service.SseService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicBinaryContentService implements BinaryContentService {

  private final JPABinaryContentRepository jpaBinaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final SseService sseService;

  @Override
  @Transactional
  public BinaryContentDto create(BinaryContentCreateRequest request) {

    log.info("[BINARY_CONTENT_CREATE] 파일 저장 시작 : fileName={}, size={}, contentType={}",
        request.fileName(), request.bytes().length, request.contentType());

    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType);
    BinaryContent savedBinaryContent = jpaBinaryContentRepository.save(binaryContent);
    eventPublisher.publishEvent(
        new BinaryContentCreatedEvent(savedBinaryContent.getId(), bytes)
    );

    log.info("[BINARY_CONTENT_CREATE] 파일 저장 완료 : binaryContentId={}", savedBinaryContent.getId());

    return binaryContentMapper.toDto(savedBinaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContentDto find(UUID binaryContentId) {
    return jpaBinaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(
            () -> new BinaryContentNotFoundException(Map.of("binaryContentId", binaryContentId)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
    return jpaBinaryContentRepository.findAllById(ids)
        .stream()
        .map(binaryContentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    jpaBinaryContentRepository.deleteById(id);
  }

  @Override
  @Transactional
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = jpaBinaryContentRepository.findById(binaryContentId)
        .orElseThrow(
            () -> new BinaryContentNotFoundException(Map.of("binaryContentId", binaryContentId)));

    binaryContent.updateStatus(status);
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    UUID receiverId = userDetails.getUserDto().id();

    sseService.send(List.of(receiverId), "binaryContents.updated",
        binaryContentMapper.toDto(binaryContent));
    return binaryContentMapper.toDto(binaryContent);
  }

}
