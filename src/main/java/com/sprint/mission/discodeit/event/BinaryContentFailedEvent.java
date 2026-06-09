package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.exception.ErrorCode;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentFailedEvent {
    private final ErrorCode error;
    private final UUID binaryContentId;

    public BinaryContentFailedEvent(ErrorCode error, UUID binaryContentId){
        this.error = error;
        this.binaryContentId = binaryContentId;
    }

}
