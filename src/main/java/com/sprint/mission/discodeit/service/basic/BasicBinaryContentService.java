package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
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

    // 먼저 파일의 메타데이터만 DB에 저장합니다.
    // 이 시점의 BinaryContent.status는 PROCESSING 입니다.
    BinaryContent binaryContent =
        new BinaryContent(
            fileName, (long) bytes.length, contentType, BinaryContentStatus.PROCESSING);
    binaryContentRepository.save(binaryContent);

    // 실제 바이너리 데이터 저장은 현재 트랜잭션이 커밋된 뒤 리스너에서 처리합니다.
    // 즉, DB 메타데이터 저장이 실패하면 파일 저장도 실행되지 않습니다.
    eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes));

    log.info(
        "바이너리 컨텐츠 생성 완료: id={}, fileName={}, size={}, status={}",
        binaryContent.getId(),
        fileName,
        bytes.length,
        binaryContent.getStatus());

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
    log.info("바이너리 컨텐츠 조회 완료: id={}, fileName={}", dto.id(), dto.fileName());
    return dto;
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("바이너리 컨텐츠 목록 조회 시작: ids={}", binaryContentIds);
    List<BinaryContentDto> dtos =
        binaryContentRepository.findAllById(binaryContentIds).stream()
            .map(binaryContentMapper::toDto)
            .toList();
    log.info("바이너리 컨텐츠 목록 조회 완료: 조회된 항목 수={}", dtos.size());
    return dtos;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent =
        binaryContentRepository
            .findById(binaryContentId)
            .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));

    // 파일 저장 리스너에서 전달받은 결과를 메타데이터 상태에 반영합니다.
    binaryContent.updateStatus(status);

    log.debug("바이너리 콘텐츠 업로드 상태 변경: id={}, status={}", binaryContentId, status);

    return binaryContentMapper.toDto(binaryContent);
  }

  @Transactional
  @Override
  public void delete(UUID binaryContentId) {
    log.debug("바이너리 컨텐츠 삭제 시작: id={}", binaryContentId);
    if (!binaryContentRepository.existsById(binaryContentId)) {
      throw BinaryContentNotFoundException.withId(binaryContentId);
    }
    binaryContentRepository.deleteById(binaryContentId);
    log.info("바이너리 컨텐츠 삭제 완료: id={}", binaryContentId);
  }
}
