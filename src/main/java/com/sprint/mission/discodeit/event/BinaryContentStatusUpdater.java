package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentStatusUpdater {

  private final BinaryContentRepository binaryContentRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateStatus(UUID binaryContentId, BinaryContentStatus status) {
    BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
        .orElseThrow(() -> BinaryContentNotFoundException.withId(binaryContentId));
    binaryContent.updateStatus(status);
    log.info("바이너리 컨텐츠 상태 업데이트: id={}, status={}", binaryContentId, status);
  }
}
