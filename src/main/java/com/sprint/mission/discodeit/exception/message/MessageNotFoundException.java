package com.sprint.mission.discodeit.exception.message;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MessageNotFoundException extends MessageException {

    public MessageNotFoundException(UUID id) {
        super(ErrorCode.MESSAGE_NOT_FOUND, Instant.now(), Map.of("id", id));
    }
}
