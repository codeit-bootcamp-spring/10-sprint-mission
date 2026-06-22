package com.sprint.mission.discodeit.binarycontent.application.handler;

import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentFailedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.provider.FileUploadResult;
import com.sprint.mission.discodeit.binarycontent.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.global.infra.storage.event.StorageCallback;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class BinaryContentStorageCallback implements StorageCallback {

  private final BinaryContentRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void handleSuccess(List<FileUploadResult> results) {
    List<UUID> successIds = results.stream()
        .filter(Predicate.not(FileUploadResult::isFailed))
        .map(FileUploadResult::id)
        .toList();
    int updatedCount = repository.updateStatus(successIds,
        BinaryContentStatus.SUCCESS);
    log.info("[FILE_UPLOAD_SUCCESS] 전체 {} 중 {}개 업로드를 성공하여 {} 로 변경",
        results.size(), updatedCount, BinaryContentStatus.SUCCESS);
    eventPublisher.publishEvent(new BinaryContentUpdatedEvent(successIds, BinaryContentStatus.SUCCESS));
  }

  @Override
  public void handleFailures(List<FileUploadResult> results) {
    Map<UUID, String> failures = results.stream()
        .filter(FileUploadResult::isFailed)
        .collect(Collectors.toMap(
            FileUploadResult::id, FileUploadResult::errorMessage
        ));
    if (failures.isEmpty()) {
      return;
    }
    log.warn("[File_UPLOAD_PARTIAL_FAILURE] 일부 파일이 업로드 실패. 상세: {}", failures);
    int updatedCount = repository.updateStatus(failures.keySet(),
        BinaryContentStatus.FAILED);
    log.info("[File_UPLOAD_FAILURE_DELETE] 업로드 실패한 {}개의 파일 중 {}개를 삭제",
        failures.size(), updatedCount);
    eventPublisher.publishEvent(new BinaryContentFailedEvent(failures));
  }
}
