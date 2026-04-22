package com.sprint.mission.discodeit.event.handler;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.FileUploadResult;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class FileUploadEventResultHandler {

  private final BinaryContentRepository binaryContentRepository;

  public void handleSuccess(List<FileUploadResult> results) {
    List<UUID> successIds = results.stream()
        .filter(Predicate.not(FileUploadResult::isFailed))
        .map(FileUploadResult::id)
        .toList();
    int updatedCount = binaryContentRepository.updateStatus(successIds,
        BinaryContentStatus.COMPLETED);
    log.info("[FILE_UPLOAD_SUCCESS] 전체 {} 중 {}개 업로드를 성공하여 {} 로 변경",
        results.size(), updatedCount, BinaryContentStatus.COMPLETED);
  }

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
    int updatedCount = binaryContentRepository.updateStatus(failures.keySet(),
        BinaryContentStatus.FAILED);
    log.info("[File_UPLOAD_FAILURE_DELETE] 업로드 실패한 {}개의 파일 중 {}개를 삭제",
        failures.size(), updatedCount);
  }
}
