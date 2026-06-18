package com.sprint.mission.discodeit.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class BinaryContentUploadFailedEvent {
    String taskName;
    String requestId;
    UUID binaryContentId;
    String errorMessage;
}
