package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class S3UploadFailedEvent {

  private final UUID binaryContentId;
  private final Throwable e;
  private final String requestId;
}
