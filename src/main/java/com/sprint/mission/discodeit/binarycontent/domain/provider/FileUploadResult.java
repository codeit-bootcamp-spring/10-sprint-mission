package com.sprint.mission.discodeit.binarycontent.domain.provider;

import java.util.UUID;

public record FileUploadResult(UUID id, boolean isFailed, String errorMessage) {

  public static FileUploadResult of(UUID id, Throwable throwable) {
    if (throwable == null) {
      return new FileUploadResult(id, false, null);
    }
    return new FileUploadResult(id, true, throwable.getCause().getMessage());
  }
}
