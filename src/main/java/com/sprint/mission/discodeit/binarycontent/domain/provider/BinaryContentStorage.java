package com.sprint.mission.discodeit.binarycontent.domain.provider;

import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.retry.annotation.Retryable;

public interface BinaryContentStorage {

  @Retryable(retryFor = DiscodeitException.class, maxAttempts = 2)
  UUID put(UUID id, byte[] bytes);

  List<FileUploadResult> putAll(Map<UUID, byte[]> files);

  String downloadUrl(UUID id);
}
