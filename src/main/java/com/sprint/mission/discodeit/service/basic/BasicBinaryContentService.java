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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
  private final BinaryContentStorage binaryContentStorage;

  private final ApplicationEventPublisher eventPublisher;

  /// (1)Thread-1
  @Transactional
  @Override
  public BinaryContentDto create(BinaryContentCreateRequest request) {
    log.debug("바이너리 컨텐츠 생성 시작: fileName={}, size={}, contentType={}", 
        request.fileName(), request.bytes().length, request.contentType());

    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    /// (2)Thread-1: 메타데이터 저장요청 -> 영속성 컨텍스트에 BinaryContent 등록
    /// BinaryContent 단독 생성은 @Transactional이 걸려있고, save와 put 작업을 같이한다.
    /// BinaryContent 메타데이터 DB 저장
    /// 문제(1) binaryContentRepository.save()시 DB 바로가는게 아니라 JPA 영속성 컨텍스트에 등록된다.
    /// binaryContentStorage.put()에는 S3 업로드 성공적으로 수행됐는데
    /// 메서드 끝나고 트랜잭션 commit 시도할때 commit 실패하면 rollback 일어난다.
    /// 그럼 s3에는 업로드 됐지만, DB에는 해당 DATA가 없는 문제가 생길 수 있다.
    binaryContentRepository.save(binaryContent);

    /// (3)Thread-1: BinaryContentCreatedEvent 발생
    /// 실제 파일을 S3에 저장.
    /// S3는 네트워크 상태, 파일크기, AWS 응답 속도에 따라 오래 걸릴 수 있다.
    /// 동시요청이 많을때 커넥션 풀이 부족해지거나 락/트랜잭션 유지 시간이 늘어날 수 있다.
    /// 바이너리 데이터 저장연산은 오래걸릴 수 있는 작업이며, 작업이 끝날때까지 트랜잭션이 대기해야한다.
    //binaryContentStorage.put(binaryContent.getId(), bytes);

    ///BinaryContentStorage 호출하는 대신 BinaryContentCreatedEvent를 발행.
    /// BinaryContentCreatedEvent를 받는 이벤트리스너로 향한다.
    eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes));

    log.info("바이너리 컨텐츠 생성 완료: id={}, fileName={}, size={}", 
        binaryContent.getId(), fileName, bytes.length);
    return binaryContentMapper.toDto(binaryContent);

    /// (4)Thread-1: 트랜잭션 commit -> DB BinaryContent 저장 확정
    /// 메타 데이터만 저장 담당.
  }

  @Override
  public BinaryContentDto find(UUID binaryContentId) {
    log.debug("바이너리 컨텐츠 조회 시작: id={}", binaryContentId);
    BinaryContentDto dto = binaryContentRepository.findById(binaryContentId)
        .map(binaryContentMapper::toDto)
        .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
    log.info("바이너리 컨텐츠 조회 완료: id={}, fileName={}", 
        dto.id(), dto.fileName());
    return dto;
  }

  @Override
  public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
    log.debug("바이너리 컨텐츠 목록 조회 시작: ids={}", binaryContentIds);
    List<BinaryContentDto> dtos = binaryContentRepository.findAllById(binaryContentIds).stream()
        .map(binaryContentMapper::toDto)
        .toList();
    log.info("바이너리 컨텐츠 목록 조회 완료: 조회된 항목 수={}", dtos.size());
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
    log.info("바이너리 컨텐츠 삭제 완료: id={}", binaryContentId);
  }

  /// BinaryContent 상태 업데이트 메소드
  /// s3에 바이너리 데이터 업로드 상태 속성 업데이트.
  /// handleBinaryContentStorage()에서 put() 성공하면 status = SUCCESS, 실패하면 status = FAIL
  /// (주의)Thread-2에서 새 DB 커넥션을 연다.
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public BinaryContentDto updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(() ->  BinaryContentNotFoundException.withId(binaryContentId));
    binaryContent.updateStatus(status);

    return  binaryContentMapper.toDto(binaryContent);
  }
}
